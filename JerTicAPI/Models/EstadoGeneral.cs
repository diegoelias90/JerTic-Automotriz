using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("estado_general")]
[Index("Nombre", Name = "UQ__estado_g__72AFBCC65FD835EC", IsUnique = true)]
public partial class EstadoGeneral
{
    [Key]
    [Column("id_estado")]
    public int IdEstado { get; set; }

    [Column("nombre")]
    [StringLength(50)]
    public string? Nombre { get; set; }

    [InverseProperty("IdEstadoNavigation")]
    public virtual ICollection<Citum> Cita { get; set; } = new List<Citum>();

    [InverseProperty("IdEstadoNavigation")]
    public virtual ICollection<Servicio> Servicios { get; set; } = new List<Servicio>();

    [InverseProperty("IdEstadoNavigation")]
    public virtual ICollection<Usuario> Usuarios { get; set; } = new List<Usuario>();
}
