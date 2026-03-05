using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class DetalleVentaController : ControllerBase
    {
        private readonly JerTicDBContext _context;

        public DetalleVentaController(JerTicDBContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> GetDetalles()
        {
            return Ok(await _context.DetalleVenta.ToListAsync());
        }

        [HttpGet("venta/{idVenta}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> GetDetallesPorVenta(int idVenta)
        {
            var detalles = await _context.DetalleVenta
                .Where(d => d.IdVenta == idVenta)
                .ToListAsync();
            return Ok(detalles);
        }

        // Usa SQL crudo para evitar el conflicto de EF Core con triggers INSTEAD OF INSERT.
        // EF Core intenta usar OUTPUT INSERTED para recuperar el ID generado,
        // pero eso es incompatible con triggers INSTEAD OF INSERT en SQL Server.
        [HttpPost]
        [Authorize(Roles = "Administrador,Cliente,Tecnico")]
        public async Task<IActionResult> CrearDetalle([FromBody] DetalleVentum detalle)
        {
            await _context.Database.ExecuteSqlRawAsync(
                "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                "VALUES ({0}, {1}, {2}, {3}, {4})",
                detalle.IdVenta,
                detalle.IdProducto,
                detalle.Cantidad,
                detalle.PrecioUnitario,
                detalle.Subtotal
            );
            return Ok(detalle);
        }

        [HttpPut("{id}")]
        [Authorize(Roles = "Administrador,Tecnico")]
        public async Task<IActionResult> EditarDetalle(int id, [FromBody] DetalleVentum detalle)
        {
            if (id != detalle.IdDetalle) return BadRequest();
            _context.Entry(detalle).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "Administrador")]
        public async Task<IActionResult> EliminarDetalle(int id)
        {
            var detalle = await _context.DetalleVenta.FindAsync(id);
            if (detalle == null) return NotFound();
            _context.DetalleVenta.Remove(detalle);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}