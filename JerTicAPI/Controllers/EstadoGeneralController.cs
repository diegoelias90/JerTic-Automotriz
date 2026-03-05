using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using JerTicAPI.Models;

[Route("api/[controller]")]
[ApiController]
[Authorize]
public class EstadoGeneralController : ControllerBase
{
    private readonly JerTicDBContext _context;

    public EstadoGeneralController(JerTicDBContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<IActionResult> GetEstados()
    {
        return Ok(await _context.EstadoGenerals.ToListAsync());
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetEstado(int id)
    {
        var estado = await _context.EstadoGenerals.FindAsync(id);

        if (estado == null)
            return NotFound();

        return Ok(estado);
    }
}