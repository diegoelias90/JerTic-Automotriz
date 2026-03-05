using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class VentaController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public VentaController(JerTicDBContext context)
        {
            _context = context;
        }

        // GET: api/Venta
        [HttpGet]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> GetVentas()
        {
            return Ok(await _context.Venta.ToListAsync());
        }

        // GET: api/Venta/5
        [HttpGet("{id}")]
        [Authorize(Roles = "Administrador, Tecnico")]
        public async Task<IActionResult> GetVenta(int id)
        {
            var venta = await _context.Venta.FindAsync(id);
            if (venta == null)
                return NotFound();
            return Ok(venta);
        }

        // POST: api/Venta
        [HttpPost]
        [Authorize(Roles = "Administrador,Cliente, Tecnico")]
        public async Task<IActionResult> CrearVenta([FromBody] Ventum venta)
        {
            venta.Fecha = DateTime.Now;
            _context.Venta.Add(venta);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetVenta), new { id = venta.IdVenta }, venta);
        }

        // PUT: api/Venta/5
        [HttpPut("{id}")]
        [Authorize(Roles = "Administrador, Tecnico")]
        public async Task<IActionResult> EditarVenta(int id, [FromBody] Ventum venta)
        {
            if (id != venta.IdVenta)
                return BadRequest("El ID de la URL no coincide con el del cuerpo.");
            _context.Entry(venta).State = EntityState.Modified;
            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Venta.Any(e => e.IdVenta == id))
                    return NotFound();
                throw;
            }
            return NoContent();
        }

        // DELETE: api/Venta/5
        [HttpDelete("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EliminarVenta(int id)
        {
            var venta = await _context.Venta.FindAsync(id);
            if (venta == null)
                return NotFound();
            _context.Venta.Remove(venta);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // ──────────────────────────────────────────────────────────────────────
        // POST: api/Venta/5/comentario
        // Usamos POST en lugar de PATCH porque HttpURLConnection de Java
        // no soporta el método PATCH nativamente.
        // ──────────────────────────────────────────────────────────────────────
        public class ComentarioRequest
        {
            public string Comentario { get; set; }
        }

        [HttpPost("{id}/comentario")]
        [Authorize(Roles = "Administrador,Cliente,Tecnico")]
        public async Task<IActionResult> AgregarComentario(int id, [FromBody] ComentarioRequest request)
        {
            if (string.IsNullOrWhiteSpace(request?.Comentario))
                return BadRequest("El comentario no puede estar vacío.");

            var venta = await _context.Venta.FindAsync(id);
            if (venta == null)
                return NotFound("Venta no encontrada.");

            // Buscar el servicio más reciente del cliente de esta venta
            var servicio = await _context.Servicios
                .Include(s => s.IdCitaNavigation)
                .Where(s => s.IdCitaNavigation != null &&
                            s.IdCitaNavigation.IdCliente == venta.IdCliente)
                .OrderByDescending(s => s.IdServicio)
                .FirstOrDefaultAsync();

            if (servicio == null)
                return UnprocessableEntity(
                    "No existe un servicio vinculado al cliente de esta venta.");

            var historial = new HistorialServicio
            {
                IdServicio = servicio.IdServicio,
                Descripcion = $"[Comentario Admin - Venta #{id}] {request.Comentario}",
                FechaEvento = DateTime.Now
            };

            _context.HistorialServicios.Add(historial);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensaje = "Comentario guardado.",
                idHistorial = historial.IdHistorial,
                idServicio = servicio.IdServicio
            });
        }
    }
}