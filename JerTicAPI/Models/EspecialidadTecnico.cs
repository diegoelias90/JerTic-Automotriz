using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("especialidad_tecnico")]
[Index("Nombre", Name = "UQ__especial__72AFBCC64B08507C", IsUnique = true)]
public partial class EspecialidadTecnico
{
    [Key]
    [Column("id_especialidad")]
    public int IdEspecialidad { get; set; }

    [Column("nombre")]
    [StringLength(100)]
    public string? Nombre { get; set; }

    [InverseProperty("IdEspecialidadNavigation")]
    public virtual ICollection<UsuarioTecnico> UsuarioTecnicos { get; set; } = new List<UsuarioTecnico>();
}
