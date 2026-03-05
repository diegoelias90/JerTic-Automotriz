
using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ProductoController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public ProductoController(JerTicDBContext context)
        {
            _context = context;
        }

        // R
        [HttpGet]
        public async Task<IActionResult> GetProductos()
        {
            return Ok(await _context.Productos.ToListAsync());
        }

        // R por id
        [HttpGet("{id}")]
        public async Task<IActionResult> GetProducto(int id)
        {
            var producto = await _context.Productos.FindAsync(id);

            if (producto == null)
                return NotFound();

            return Ok(producto);
        }

        // C
        [HttpPost]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> CrearProducto(Producto producto)
        {
            producto.FechaCreacion = DateTime.Now;

            _context.Productos.Add(producto);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetProducto),
                new { id = producto.IdProducto }, producto);
        }

        // U
        [HttpPut("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EditarProducto(int id, Producto producto)
        {
            if (id != producto.IdProducto)
                return BadRequest();

            _context.Entry(producto).State = EntityState.Modified;
            await _context.SaveChangesAsync();

            return NoContent();
        }

        // D
        [HttpDelete("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EliminarProducto(int id)
        {
            var producto = await _context.Productos.FindAsync(id);

            if (producto == null)
                return NotFound();

            _context.Productos.Remove(producto);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}