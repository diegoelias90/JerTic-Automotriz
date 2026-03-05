using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("usuario")]
[Index("Correo", Name = "UQ__usuario__2A586E0BAF440E95", IsUnique = true)]
public partial class Usuario
{
    [Key]
    [Column("id_usuario")]
    public int IdUsuario { get; set; }

    [Column("nombre")]
    [StringLength(100)]
    public string? Nombre { get; set; }

    [Column("apellido")]
    [StringLength(100)]
    public string? Apellido { get; set; }

    [Column("correo")]
    [StringLength(150)]
    public string? Correo { get; set; }

    [Column("telefono")]
    [StringLength(20)]
    public string? Telefono { get; set; }

    [Column("token")]
    [StringLength(255)]
    public string? Token { get; set; }

    [Column("fecha_registro", TypeName = "datetime")]
    public DateTime? FechaRegistro { get; set; }

    [Column("id_tipo_usuario")]
    public int? IdTipoUsuario { get; set; }

    [Column("id_estado")]
    public int? IdEstado { get; set; }

    [ForeignKey("IdEstado")]
    [InverseProperty("Usuarios")]
    public virtual EstadoGeneral? IdEstadoNavigation { get; set; }

    [ForeignKey("IdTipoUsuario")]
    [InverseProperty("Usuarios")]
    public virtual TipoUsuario? IdTipoUsuarioNavigation { get; set; }

    [InverseProperty("IdUsuarioNavigation")]
    public virtual UsuarioCliente? UsuarioCliente { get; set; }

    [InverseProperty("IdUsuarioNavigation")]
    public virtual UsuarioTecnico? UsuarioTecnico { get; set; }
}
