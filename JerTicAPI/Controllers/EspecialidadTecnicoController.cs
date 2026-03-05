
using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class EspecialidadTecnicoController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public EspecialidadTecnicoController(JerTicDBContext context)
        {
            _context = context;
        }

        // R
        [HttpGet]
        public async Task<IActionResult> GetEspecialidades()
        {
            return Ok(await _context.EspecialidadTecnicos.ToListAsync());
        }

        // C
        [HttpPost]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> CrearEspecialidad(EspecialidadTecnico especialidad)
        {
            _context.EspecialidadTecnicos.Add(especialidad);
            await _context.SaveChangesAsync();

            return Ok(especialidad);
        }
    }
}