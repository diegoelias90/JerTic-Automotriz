using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

public class estado_general
{
    [Key]
    [Column("id_estado")]
    public int id_estado { get; set; }

    public string nombre { get; set; }
}