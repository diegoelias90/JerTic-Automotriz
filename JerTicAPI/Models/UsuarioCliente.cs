using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("usuario_cliente")]
[Index("IdUsuario", Name = "UQ__usuario___4E3E04AC4359ACC7", IsUnique = true)]
public partial class UsuarioCliente
{
    [Key]
    [Column("id_cliente")]
    public int IdCliente { get; set; }

    [Column("id_usuario")]
    public int? IdUsuario { get; set; }

    [InverseProperty("IdClienteNavigation")]
    public virtual ICollection<Citum> Cita { get; set; } = new List<Citum>();

    [ForeignKey("IdUsuario")]
    [InverseProperty("UsuarioCliente")]
    public virtual Usuario? IdUsuarioNavigation { get; set; }

    [InverseProperty("IdClienteNavigation")]
    public virtual ICollection<Ventum> Venta { get; set; } = new List<Ventum>();
}
