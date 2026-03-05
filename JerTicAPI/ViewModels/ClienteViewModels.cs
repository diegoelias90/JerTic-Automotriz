using System.ComponentModel.DataAnnotations;

namespace JerTicAPI.ViewModels
{
    public class LoginViewModel
    {
        [Required(ErrorMessage = "El correo es obligatorio.")]
        [EmailAddress(ErrorMessage = "Formato de correo invalido.")]
        [Display(Name = "Correo electronico")]
        public string Correo { get; set; } = "";

        [Required(ErrorMessage = "La contrasena es obligatoria.")]
        [Display(Name = "Contrasena")]
        public string Token { get; set; } = "";

        public string? ReturnUrl { get; set; }
        public string? Error { get; set; }
    }

    public class RegistroViewModel
    {
        [Required(ErrorMessage = "El nombre es obligatorio.")]
        [Display(Name = "Nombre")]
        public string Nombre { get; set; } = "";

        [Required(ErrorMessage = "El apellido es obligatorio.")]
        [Display(Name = "Apellido")]
        public string Apellido { get; set; } = "";

        [Required(ErrorMessage = "El correo es obligatorio.")]
        [EmailAddress(ErrorMessage = "Formato de correo invalido.")]
        [Display(Name = "Correo electronico")]
        public string Correo { get; set; } = "";

        [Display(Name = "Telefono")]
        public string? Telefono { get; set; }

        [Required(ErrorMessage = "La contrasena es obligatoria.")]
        [MinLength(6, ErrorMessage = "Minimo 6 caracteres.")]
        [Display(Name = "Contrasena")]
        public string Token { get; set; } = "";

        [Compare("Token", ErrorMessage = "Las contrasenas no coinciden.")]
        [Display(Name = "Confirmar contrasena")]
        public string ConfirmarToken { get; set; } = "";

        public string? Error { get; set; }
    }

    public class CitaViewModel
    {
        [Required(ErrorMessage = "La fecha es obligatoria.")]
        [Display(Name = "Fecha")]
        [DataType(DataType.Date)]
        public DateTime Fecha { get; set; } = DateTime.Today.AddDays(1);

        [Display(Name = "Hora")]
        public string? Hora { get; set; }

        [Required(ErrorMessage = "El motivo es obligatorio.")]
        [Display(Name = "Motivo")]
        [MaxLength(500)]
        public string Motivo { get; set; } = "";

        public string? Error { get; set; }
        public string? Exito { get; set; }
    }

    public class PerfilViewModel
    {
        public string Nombre { get; set; } = "";
        public string Apellido { get; set; } = "";
        public string Correo { get; set; } = "";
        public string Telefono { get; set; } = "";
        public DateTime? FechaRegistro { get; set; }
        public string? Error { get; set; }
        public string? Exito { get; set; }
    }

    public class HistorialViewModel
    {
        public List<CitaResumen> Citas { get; set; } = new();
        public List<ServicioResumen> Servicios { get; set; } = new();
        public List<HistorialItem> Historial { get; set; } = new();
    }

    public class CitaResumen
    {
        public int IdCita { get; set; }
        public DateTime? Fecha { get; set; }
        public string? Hora { get; set; }
        public string? Motivo { get; set; }
        public int? IdEstado { get; set; }
        public string NombreEstado => IdEstado switch
        {
            3 => "Pendiente",
            4 => "Programada",
            5 => "En proceso",
            6 => "Finalizado",
            7 => "Cancelada",
            _ => "—"
        };
        public string CssEstado => IdEstado switch
        {
            4 => "estado-programada",
            5 => "estado-proceso",
            6 => "estado-finalizado",
            7 => "estado-cancelada",
            _ => "estado-pendiente"
        };
    }

    public class ServicioResumen
    {
        public int IdServicio { get; set; }
        public int? IdCita { get; set; }
        public string? Descripcion { get; set; }
        public decimal? Costo { get; set; }
        public int? IdEstado { get; set; }
        public string NombreEstado => IdEstado switch
        {
            3 => "Pendiente",
            4 => "Programada",
            5 => "En proceso",
            6 => "Finalizado",
            7 => "Cancelado",
            _ => "—"
        };
    }

    public class HistorialItem
    {
        public int IdHistorial { get; set; }
        public int? IdServicio { get; set; }
        public string? Descripcion { get; set; }
        public DateTime? FechaEvento { get; set; }
    }
}