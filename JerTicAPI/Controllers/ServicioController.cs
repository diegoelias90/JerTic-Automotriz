using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    // IMPORTANTE: Solo debe existir UNA clase ServicioController.
    // Eliminá la que tenía "public class ServicioController : Controller" con el Index().

    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ServicioController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public ServicioController(JerTicDBContext context)
        {
            _context = context;
        }

        // GET: api/Servicio
        [HttpGet]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> GetServicios()
        {
            return Ok(await _context.Servicios.ToListAsync());
        }

        // GET: api/Servicio/5
        [HttpGet("{id}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> GetServicio(int id)
        {
            var servicio = await _context.Servicios.FindAsync(id);
            if (servicio == null)
                return NotFound();
            return Ok(servicio);
        }

        // POST: api/Servicio
        // Usado por el admin cuando asigna un técnico a una cita
        [HttpPost]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> CrearServicio([FromBody] Servicio servicio)
        {
            // Fechas automáticas al crear
            servicio.FechaInicio = DateTime.Now;
            servicio.FechaFin = DateTime.Now; // se actualizará al finalizar

            _context.Servicios.Add(servicio);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetServicio),
                new { id = servicio.IdServicio }, servicio);
        }

        // PUT: api/Servicio/5
        [HttpPut("{id}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> EditarServicio(int id, [FromBody] Servicio servicio)
        {
            if (id != servicio.IdServicio)
                return BadRequest("El ID de la URL no coincide con el del cuerpo.");

            _context.Entry(servicio).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Servicios.Any(e => e.IdServicio == id))
                    return NotFound();
                else
                    throw;
            }

            return NoContent();
        }

        // DELETE: api/Servicio/5
        [HttpDelete("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EliminarServicio(int id)
        {
            var servicio = await _context.Servicios.FindAsync(id);
            if (servicio == null)
                return NotFound();

            _context.Servicios.Remove(servicio);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}