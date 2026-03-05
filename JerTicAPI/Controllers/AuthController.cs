using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using JerTicAPI.Models;

namespace JerTicAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly IConfiguration _config;
        private readonly JerTicDBContext _context;

        public AuthController(IConfiguration config, JerTicDBContext context)
        {
            _config = config;
            _context = context;
        }

        public class LoginRequest
        {
            public string correo { get; set; }
            public string token { get; set; }
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest request)
        {
            var usuario = _context.Usuarios
                .FirstOrDefault(u => u.Correo == request.correo && 
                u.Token == request.token &&
                u.IdEstado == 1);

            if (usuario == null)
                return Unauthorized("Credenciales inválidas");

            // Determinar rol manualmente según id_tipo_usuario
            string rol = usuario.IdTipoUsuario switch
            {
                1 => "Administrador",
                2 => "Tecnico",
                3 => "Cliente",
                _ => "Cliente"
            };

            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, usuario.IdUsuario.ToString()),
                new Claim(ClaimTypes.Email, usuario.Correo),
                new Claim(ClaimTypes.Role, rol)
            };

            var key = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(_config["Jwt:Key"]));

            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: _config["Jwt:Issuer"],
                audience: _config["Jwt:Audience"],
                claims: claims,
                expires: DateTime.Now.AddMinutes(
                    Convert.ToDouble(_config["Jwt:ExpireMinutes"])),
                signingCredentials: creds
            );

            return Ok(new
            {
                token = new JwtSecurityTokenHandler().WriteToken(token),
                rol = rol,
                nombre = usuario.Nombre
            });
        }
    }
}