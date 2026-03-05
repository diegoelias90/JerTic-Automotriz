using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("venta")]
public partial class Ventum
{
    [Key]
    [Column("id_venta")]
    public int IdVenta { get; set; }

    [Column("id_cliente")]
    public int? IdCliente { get; set; }

    [Column("fecha", TypeName = "datetime")]
    public DateTime? Fecha { get; set; }

    [Column("total", TypeName = "decimal(10, 2)")]
    public decimal? Total { get; set; }

    [InverseProperty("IdVentaNavigation")]
    public virtual ICollection<DetalleVentum> DetalleVenta { get; set; } = new List<DetalleVentum>();

    [ForeignKey("IdCliente")]
    [InverseProperty("Venta")]
    public virtual UsuarioCliente? IdClienteNavigation { get; set; }
}
