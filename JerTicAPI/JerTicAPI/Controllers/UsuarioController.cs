using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using JerTicAPI.Data;
using JerTicAPI.Models;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuarioController : ControllerBase
    {
        private readonly JerticDBContext _context;
        public UsuarioController(JerticDBContext context)
        {
            _context = context; 
        }

        //GET
        [HttpGet]
        public IActionResult GetUsuarios()
        {
            var usuarios = _context.Usuario.ToList();
            return Ok(usuarios);
        }

        // GET: api/Ventas/5
        [HttpGet("{id_usuario}")]
        public IActionResult GetUsuario(int id_usuario)
        {
            var usuario = _context.Usuario.Find(id_usuario);
            if (usuario == null)
            {
                return NotFound();
            }
            return Ok(usuario);
        }

        // POST: api/Usuario
        [HttpPost]
        public IActionResult PostUsuario([FromBody] usuario usuario)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            //var proveedorExiste = _context.Proveedores.Any(p => p.idProveedor == producto.idProveedor);
            //if (!proveedorExiste)
            //    return BadRequest(new { message = "El idProveedor no existe." });

            //producto.activo = true;

            _context.Usuario.Add(usuario);
            _context.SaveChanges();

            return CreatedAtAction(nameof(GetUsuario), new { id_usuario = usuario.id_usuario }, usuario);
        }
    }
}
