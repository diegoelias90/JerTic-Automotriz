using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("usuario_tecnico")]
[Index("IdUsuario", Name = "UQ__usuario___4E3E04ACB0253287", IsUnique = true)]
public partial class UsuarioTecnico
{
    [Key]
    [Column("id_tecnico")]
    public int IdTecnico { get; set; }

    [Column("id_usuario")]
    public int? IdUsuario { get; set; }

    [Column("id_especialidad")]
    public int? IdEspecialidad { get; set; }

    [ForeignKey("IdEspecialidad")]
    [InverseProperty("UsuarioTecnicos")]
    public virtual EspecialidadTecnico? IdEspecialidadNavigation { get; set; }

    [ForeignKey("IdUsuario")]
    [InverseProperty("UsuarioTecnico")]
    public virtual Usuario? IdUsuarioNavigation { get; set; }

    [InverseProperty("IdTecnicoNavigation")]
    public virtual ICollection<Servicio> Servicios { get; set; } = new List<Servicio>();
}
