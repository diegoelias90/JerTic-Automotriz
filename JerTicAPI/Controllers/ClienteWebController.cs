using System.Security.Claims;
using System.Text;
using System.Text.RegularExpressions;
using JerTicAPI.Models;
using JerTicAPI.ViewModels;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    public class ClienteWebController : Controller
    {
        private readonly JerTicDBContext _db;

        public ClienteWebController(JerTicDBContext db)
        {
            _db = db;
        }

        private int IdUsuarioActual =>
            int.TryParse(User.FindFirstValue(ClaimTypes.NameIdentifier), out int id) ? id : 0;

        private async Task<UsuarioCliente?> GetClienteActual() =>
            await _db.UsuarioClientes.FirstOrDefaultAsync(c => c.IdUsuario == IdUsuarioActual);

        // Normalizar: Normalize() es metodo de string,
        private static string Normalizar(string s)
        {
            string formD = s.Trim().Normalize(NormalizationForm.FormD);
            return Regex.Replace(formD, @"\p{Mn}", "").ToLowerInvariant();
        }

        // ── INDEX ─────────────────────────────────────────────────────────
        [HttpGet]
        public IActionResult Index() => View();

        // ── PRODUCTOS ─────────────────────────────────────────────────────
        [HttpGet]
        public async Task<IActionResult> Productos()
        {
            var productos = await _db.Productos
                .Include(p => p.IdTipoProductoNavigation)
                .Where(p => p.Stock > 0)
                .OrderBy(p => p.Nombre)
                .ToListAsync();
            return View(productos);
        }

        // ── LOGIN ─────────────────────────────────────────────────────────
        [HttpGet]
        public IActionResult Login(string? returnUrl = null)
        {
            if (User.Identity?.IsAuthenticated == true)
                return RedirectToAction("Index");
            return View(new LoginViewModel { ReturnUrl = returnUrl });
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Login(LoginViewModel vm)
        {
            if (!ModelState.IsValid) return View(vm);

            var usuario = await _db.Usuarios
                .FirstOrDefaultAsync(u => u.Correo == vm.Correo);

            if (usuario == null || usuario.Token != vm.Token)
            {
                vm.Error = "Correo o contrasena incorrectos.";
                return View(vm);
            }

            // TipoUsuario.Nombre (no NombreTipo)
            var tipoUsuario = await _db.TipoUsuarios.FindAsync(usuario.IdTipoUsuario);
            string rolNorm = Normalizar(tipoUsuario?.Nombre ?? "");
            if (rolNorm != "cliente")
            {
                vm.Error = "Esta plataforma es solo para clientes.";
                return View(vm);
            }

            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.NameIdentifier, usuario.IdUsuario.ToString()),
                new Claim(ClaimTypes.Name,  $"{usuario.Nombre} {usuario.Apellido}"),
                new Claim(ClaimTypes.Email, usuario.Correo ?? ""),
                new Claim(ClaimTypes.Role,  "Cliente")
            };

            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            var principal = new ClaimsPrincipal(identity);

            await HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme, principal,
                new AuthenticationProperties
                {
                    IsPersistent = true,
                    ExpiresUtc = DateTimeOffset.UtcNow.AddHours(8)
                });

            TempData["Exito"] = $"Bienvenido, {usuario.Nombre}.";
            return Redirect(vm.ReturnUrl ?? "/ClienteWeb/Index");
        }

        // ── REGISTRO ──────────────────────────────────────────────────────
        [HttpGet]
        public IActionResult Registro() => View(new RegistroViewModel());

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Registro(RegistroViewModel vm)
        {
            if (!ModelState.IsValid) return View(vm);

            if (await _db.Usuarios.AnyAsync(u => u.Correo == vm.Correo))
            {
                vm.Error = "El correo ya esta registrado.";
                return View(vm);
            }

            var usuario = new Usuario
            {
                Nombre = vm.Nombre,
                Apellido = vm.Apellido,
                Correo = vm.Correo,
                Telefono = vm.Telefono,
                Token = vm.Token,
                IdTipoUsuario = 3,
                IdEstado = 1,
                FechaRegistro = DateTime.Now
            };
            _db.Usuarios.Add(usuario);
            await _db.SaveChangesAsync();

            _db.UsuarioClientes.Add(new UsuarioCliente { IdUsuario = usuario.IdUsuario });
            await _db.SaveChangesAsync();

            TempData["Exito"] = "Cuenta creada. Ahora podes iniciar sesion.";
            return RedirectToAction("Login");
        }

        // ── LOGOUT ────────────────────────────────────────────────────────
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Logout()
        {
            await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
            return RedirectToAction("Index");
        }

        // ── CITA ──────────────────────────────────────────────────────────
        [HttpGet]
        [Authorize(Policy = "WebCliente")]
        public IActionResult Cita() => View(new CitaViewModel());

        [HttpPost]
        [ValidateAntiForgeryToken]
        [Authorize(Policy = "WebCliente")]
        public async Task<IActionResult> Cita(CitaViewModel vm)
        {
            if (!ModelState.IsValid) return View(vm);

            if (vm.Fecha.Date <= DateTime.Today)
            {
                vm.Error = "La fecha debe ser a partir de manana.";
                return View(vm);
            }

            var cliente = await GetClienteActual();
            if (cliente == null)
            {
                vm.Error = "No se encontro tu perfil de cliente.";
                return View(vm);
            }

            // Convertir DateTime → DateOnly  y  string → TimeOnly?
            var fechaCita = DateOnly.FromDateTime(vm.Fecha);
            TimeOnly? horaCita = null;
            if (!string.IsNullOrEmpty(vm.Hora) &&
                TimeOnly.TryParse(vm.Hora, out TimeOnly horaParseada))
            {
                horaCita = horaParseada;
            }

            var cita = new Citum
            {
                IdCliente = cliente.IdCliente,
                Fecha = fechaCita,
                Hora = horaCita,
                Motivo = vm.Motivo,
                IdEstado = 3
            };
            _db.Cita.Add(cita);
            await _db.SaveChangesAsync();

            return View(new CitaViewModel
            {
                Exito = $"Cita agendada para el {vm.Fecha:dd/MM/yyyy}. Pronto nos comunicaremos para confirmar."
            });
        }

        // ── HISTORIAL ─────────────────────────────────────────────────────
        [HttpGet]
        [Authorize(Policy = "WebCliente")]
        public async Task<IActionResult> Historial()
        {
            var cliente = await GetClienteActual();
            if (cliente == null) return RedirectToAction("Login");

            var citas = await _db.Cita
                .Where(c => c.IdCliente == cliente.IdCliente)
                .OrderByDescending(c => c.Fecha)
                .ToListAsync();

            var idCitas = citas.Select(c => c.IdCita).ToList();

            var servicios = await _db.Servicios
                .Where(s => s.IdCita != null && idCitas.Contains(s.IdCita.Value))
                .ToListAsync();

            var idServicios = servicios.Select(s => s.IdServicio).ToList();

            var historial = await _db.HistorialServicios
                .Where(h => h.IdServicio != null && idServicios.Contains(h.IdServicio.Value))
                .OrderByDescending(h => h.FechaEvento)
                .ToListAsync();

            var vm = new HistorialViewModel
            {
                Citas = citas.Select(c => new CitaResumen
                {
                    IdCita = c.IdCita,
                    // DateOnly? → DateTime? para el ViewModel
                    Fecha = c.Fecha.HasValue ? c.Fecha.Value.ToDateTime(TimeOnly.MinValue) : (DateTime?)null,
                    // TimeOnly? → string para el ViewModel
                    Hora = c.Hora.HasValue ? c.Hora.Value.ToString("HH:mm") : null,
                    Motivo = c.Motivo,
                    IdEstado = c.IdEstado
                }).ToList(),

                Servicios = servicios.Select(s => new ServicioResumen
                {
                    IdServicio = s.IdServicio,
                    IdCita = s.IdCita,
                    Descripcion = s.Descripcion,
                    Costo = s.Costo,
                    IdEstado = s.IdEstado
                }).ToList(),

                Historial = historial.Select(h => new HistorialItem
                {
                    IdHistorial = h.IdHistorial,
                    IdServicio = h.IdServicio,
                    Descripcion = h.Descripcion,
                    FechaEvento = h.FechaEvento
                }).ToList()
            };

            return View(vm);
        }

        // ── PERFIL ────────────────────────────────────────────────────────
        [HttpGet]
        [Authorize(Policy = "WebCliente")]
        public async Task<IActionResult> Perfil()
        {
            var usuario = await _db.Usuarios.FindAsync(IdUsuarioActual);
            if (usuario == null) return RedirectToAction("Login");

            return View(new PerfilViewModel
            {
                Nombre = usuario.Nombre ?? "",
                Apellido = usuario.Apellido ?? "",
                Correo = usuario.Correo ?? "",
                Telefono = usuario.Telefono ?? "",
                FechaRegistro = usuario.FechaRegistro,
                Exito = TempData["Exito"]?.ToString()
            });
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        [Authorize(Policy = "WebCliente")]
        public async Task<IActionResult> Perfil(PerfilViewModel vm)
        {
            if (!ModelState.IsValid) return View(vm);

            var usuario = await _db.Usuarios.FindAsync(IdUsuarioActual);
            if (usuario == null) { vm.Error = "Usuario no encontrado."; return View(vm); }

            usuario.Nombre = vm.Nombre;
            usuario.Apellido = vm.Apellido;
            usuario.Telefono = vm.Telefono;
            await _db.SaveChangesAsync();

            var claims = User.Claims.ToList();
            claims.RemoveAll(c => c.Type == ClaimTypes.Name);
            claims.Add(new Claim(ClaimTypes.Name, $"{vm.Nombre} {vm.Apellido}"));
            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            await HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                new ClaimsPrincipal(identity),
                new AuthenticationProperties { IsPersistent = true, ExpiresUtc = DateTimeOffset.UtcNow.AddHours(8) });

            vm.Exito = "Perfil actualizado.";
            return View(vm);
        }
    }
}