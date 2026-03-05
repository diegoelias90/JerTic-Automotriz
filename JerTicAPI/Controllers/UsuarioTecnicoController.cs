using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class UsuarioTecnicoController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public UsuarioTecnicoController(JerTicDBContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Authorize(Roles = "Administrador,Tecnico")]
        public IActionResult GetAll()
        {
            return Ok(_context.UsuarioTecnicos.ToList());
        }

        [HttpGet("{id}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public IActionResult GetById(int id)
        {
            var tecnico = _context.UsuarioTecnicos.Find(id);
            if (tecnico == null) return NotFound();
            return Ok(tecnico);
        }
    }
}