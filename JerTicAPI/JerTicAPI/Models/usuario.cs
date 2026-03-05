using System.ComponentModel.DataAnnotations;

public class usuario
{
    [Key]
    public int id_usuario { get; set; }

    public string nombre { get; set; }
    public string apellido { get; set; }
    public string correo { get; set; }
    public string telefono { get; set; }
    public string token { get; set; }
    public DateTime fecha_registro { get; set; }

    public int id_tipo_usuario { get; set; }
    public int id_estado { get; set; }
}