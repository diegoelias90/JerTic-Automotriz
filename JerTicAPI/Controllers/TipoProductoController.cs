using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class TipoProductoController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public TipoProductoController(JerTicDBContext context)
        {
            _context = context;
        }

        // GET: api/TipoProducto
        [HttpGet]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> GetTipos()
        {
            return Ok(await _context.TipoProductos.ToListAsync());
        }

        // GET: api/TipoProducto/5
        [HttpGet("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> GetTipo(int id)
        {
            var tipo = await _context.TipoProductos.FindAsync(id);
            if (tipo == null)
                return NotFound();
            return Ok(tipo);
        }

        // POST: api/TipoProducto
        [HttpPost]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> CrearTipo([FromBody] TipoProducto tipo)
        {
            _context.TipoProductos.Add(tipo);
            await _context.SaveChangesAsync();
            return Ok(tipo);
        }

        // PUT: api/TipoProducto/5
        [HttpPut("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EditarTipo(int id, [FromBody] TipoProducto tipo)
        {
            if (id != tipo.IdTipoProducto)
                return BadRequest();
            _context.Entry(tipo).State = EntityState.Modified;
            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.TipoProductos.Any(t => t.IdTipoProducto == id))
                    return NotFound();
                throw;
            }
            return NoContent();
        }

        // DELETE: api/TipoProducto/5
        // Si hay productos usando este tipo, SQL Server lanzará un error de FK.
        // El catch lo captura y devuelve 409 Conflict con mensaje claro.
        [HttpDelete("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EliminarTipo(int id)
        {
            var tipo = await _context.TipoProductos.FindAsync(id);
            if (tipo == null)
                return NotFound();

            try
            {
                _context.TipoProductos.Remove(tipo);
                await _context.SaveChangesAsync();
                return NoContent();
            }
            catch (DbUpdateException)
            {
                // Violación de FK: hay productos que usan este tipo
                return Conflict("No se puede eliminar: hay productos asociados a este tipo.");
            }
        }
    }
}