using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class CitaController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public CitaController(JerTicDBContext context)
        {
            _context = context;
        }

        // GET: api/Cita
        [HttpGet]
        [Authorize(Roles = "Administrador,Tecnico,Cliente")]
        public async Task<IActionResult> GetCitas()
        {
            var citas = await _context.Cita.ToListAsync();
            return Ok(citas);
        }

        // GET: api/Cita/5
        [HttpGet("{id}")]
        [Authorize(Roles = "Administrador,Tecnico,Cliente")]
        public async Task<IActionResult> GetCita(int id)
        {
            var cita = await _context.Cita.FindAsync(id);
            if (cita == null)
                return NotFound();
            return Ok(cita);
        }

        // POST: api/Cita
        [HttpPost]
        [Authorize(Roles = "Administrador,Tecnico,Cliente")]
        public async Task<IActionResult> CrearCita([FromBody] Citum nuevaCita)
        {
            _context.Cita.Add(nuevaCita);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetCita), new { id = nuevaCita.IdCita }, nuevaCita);
        }

        // PUT: api/Cita/5
        // Recibe el objeto Citum completo — usado para actualizar estado también
        [HttpPut("{id}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> EditarCita(int id, [FromBody] Citum citaActualizada)
        {
            if (id != citaActualizada.IdCita)
                return BadRequest("El ID de la URL no coincide con el del cuerpo.");

            _context.Entry(citaActualizada).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Cita.Any(e => e.IdCita == id))
                    return NotFound();
                else
                    throw;
            }

            return NoContent();
        }

        [HttpPut("{id}/estado")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> CambiarEstado(int id, [FromBody] EstadoDto dto)
        {
            var cita = await _context.Cita.FindAsync(id);
            if (cita == null)
                return NotFound();

            cita.IdEstado = dto.IdEstado;

            await _context.SaveChangesAsync();

            return NoContent();
        }

        public class EstadoDto
        {
            public int IdEstado { get; set; }
        }

        // DELETE: api/Cita/5
        [HttpDelete("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EliminarCita(int id)
        {
            var cita = await _context.Cita.FindAsync(id);
            if (cita == null)
                return NotFound();

            _context.Cita.Remove(cita);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}