using JerTicAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[Route("api/[controller]")]
[ApiController]
[Authorize]
public class TipoUsuarioController : ControllerBase
{
    private readonly JerTicDBContext _context;

    public TipoUsuarioController(JerTicDBContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<IActionResult> GetTiposUsuario()
    {
        return Ok(await _context.TipoUsuarios.ToListAsync());
    }
}