using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

public class tipo_usuario
{
    [Key]
    [Column("id_tipo_usuario")]
    public int id_tipo_usuario { get; set; }

    public string nombre { get; set; }
}