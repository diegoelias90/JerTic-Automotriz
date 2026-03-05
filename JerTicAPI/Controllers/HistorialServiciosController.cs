using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[Route("api/[controller]")]
[ApiController]
[Authorize]
public class HistorialServiciosController : ControllerBase
{
    private readonly JerTicDBContext _context;

    public HistorialServiciosController(JerTicDBContext context)
    {
        _context = context;
    }

    // R
    [HttpGet]
    public async Task<IActionResult> GetHistorial()
    {
        return Ok(await _context.HistorialServicios.ToListAsync());
    }

    // C
    [HttpPost]
    public async Task<IActionResult> CrearHistorial(HistorialServicio historial)
    {
        historial.FechaEvento = DateTime.Now;

        _context.HistorialServicios.Add(historial);
        await _context.SaveChangesAsync();

        return Ok(historial);
    }
}