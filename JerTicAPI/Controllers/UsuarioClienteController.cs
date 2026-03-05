using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class UsuarioClienteController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public UsuarioClienteController(JerTicDBContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Authorize(Roles = "Administrador,Tecnico")]
        public IActionResult GetAll()
        {
            return Ok(_context.UsuarioClientes.ToList());
        }

        [HttpGet("{id}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public IActionResult GetById(int id)
        {
            var cliente = _context.UsuarioClientes.Find(id);
            if (cliente == null) return NotFound();
            return Ok(cliente);
        }
    }
}