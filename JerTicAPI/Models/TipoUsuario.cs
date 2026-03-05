using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("tipo_usuario")]
[Index("Nombre", Name = "UQ__tipo_usu__72AFBCC64D5C2291", IsUnique = true)]
public partial class TipoUsuario
{
    [Key]
    [Column("id_tipo_usuario")]
    public int IdTipoUsuario { get; set; }

    [Column("nombre")]
    [StringLength(50)]
    public string? Nombre { get; set; }

    [InverseProperty("IdTipoUsuarioNavigation")]
    public virtual ICollection<Usuario> Usuarios { get; set; } = new List<Usuario>();
}
