using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Text.RegularExpressions;
using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuarioController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public UsuarioController(JerTicDBContext context)
        {
            _context = context;
        }

        private bool CorreoValido(string correo)
        {
            if (string.IsNullOrWhiteSpace(correo))
                return false;

            var pattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
            return Regex.IsMatch(correo, pattern);
        }

        [Authorize(Roles = "Administrador, Tecnico")]
        [HttpGet]
        public IActionResult GetUsuarios()
        {
            return Ok(_context.Usuarios.ToList());
        }

        [HttpGet("{id_usuario}")]
        public IActionResult GetUsuario(int id_usuario)
        {
            var usuario = _context.Usuarios.Find(id_usuario);
            if (usuario == null)
                return NotFound(new { message = "Usuario no encontrado" });

            return Ok(usuario);
        }

        [Authorize(Roles = "Administrador, Cliente")]
        [HttpPost]
        public IActionResult PostUsuario([FromBody] Usuario usuario)
        {
            if (usuario == null)
                return BadRequest(new { message = "Datos inválidos" });

            if (!CorreoValido(usuario.Correo))
                return BadRequest(new { message = "Formato de correo inválido" });

            if (_context.Usuarios.Any(u => u.Correo == usuario.Correo))
                return Conflict(new { message = "Correo ya registrado" });

            usuario.FechaRegistro = DateTime.Now;

            _context.Usuarios.Add(usuario);
            _context.SaveChanges();

            return CreatedAtAction(nameof(GetUsuario),
                new { id_usuario = usuario.IdUsuario }, usuario);
        }


        [HttpPut("{id}")]
        public IActionResult UpdateUsuario(int id, [FromBody] Usuario usuario)
        {
            var existing = _context.Usuarios.Find(id);
            if (existing == null)
                return NotFound(new { message = "Usuario no encontrado" });

            existing.Nombre = usuario.Nombre;
            existing.Apellido = usuario.Apellido;
            existing.Correo = usuario.Correo;
            existing.Telefono = usuario.Telefono;
            existing.IdTipoUsuario = usuario.IdTipoUsuario;
            existing.IdEstado = usuario.IdEstado;

            _context.SaveChanges();

            return Ok(existing);
        }

        //SOLO ADMIN PUEDE ELIMINAR
        [Authorize(Roles = "Administrador")]
        [HttpDelete("{id}")]
        public IActionResult DeleteUsuario(int id)
        {
            var usuario = _context.Usuarios.Find(id);
            if (usuario == null)
                return NotFound(new { message = "Usuario no encontrado" });

            var estadoInactivo = _context.EstadoGenerals
                .FirstOrDefault(e => e.Nombre == "Inactivo");

            usuario.IdEstado = estadoInactivo.IdEstado;

            _context.SaveChanges();

            return Ok(new { message = "Usuario marcado como inactivo" });
        }
    }
}