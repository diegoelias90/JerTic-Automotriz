using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[Route("api/[controller]")]
[ApiController]
[Authorize]
public class TipoServicioController : ControllerBase
{
    private readonly JerTicDBContext _context;

    public TipoServicioController(JerTicDBContext context)
    {
        _context = context;
    }

    // R
    [Authorize(Roles = "Administrador, Cliente, Tecnico")]
    [HttpGet]
    public async Task<IActionResult> GetTiposServicio()
    {
        return Ok(await _context.TipoServicios.ToListAsync());
    }

    // C
    [HttpPost]
    [Authorize(Roles = "Administrador, Cliente, Tecnico")]
    public async Task<IActionResult> CrearTipoServicio(TipoServicio tipo)
    {
        _context.TipoServicios.Add(tipo);
        await _context.SaveChangesAsync();

        return Ok(tipo);
    }

    // U
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador, Cliente, Tecnico")]
    public async Task<IActionResult> EditarTipoServicio(int id, TipoServicio tipo)
    {
        if (id != tipo.IdTipoServicio)
            return BadRequest();

        _context.Entry(tipo).State = EntityState.Modified;
        await _context.SaveChangesAsync();

        return NoContent();
    }
}