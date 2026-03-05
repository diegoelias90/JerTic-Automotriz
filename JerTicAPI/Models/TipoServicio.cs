using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("tipo_servicio")]
[Index("Nombre", Name = "UQ__tipo_ser__72AFBCC627BD8BD5", IsUnique = true)]
public partial class TipoServicio
{
    [Key]
    [Column("id_tipo_servicio")]
    public int IdTipoServicio { get; set; }

    [Column("nombre")]
    [StringLength(100)]
    public string? Nombre { get; set; }

    [Column("descripcion")]
    public string? Descripcion { get; set; }

    [InverseProperty("IdTipoServicioNavigation")]
    public virtual ICollection<Servicio> Servicios { get; set; } = new List<Servicio>();
}
