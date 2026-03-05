using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("detalle_venta")]
public partial class DetalleVentum
{
    [Key]
    [Column("id_detalle")]
    public int IdDetalle { get; set; }

    [Column("id_venta")]
    public int? IdVenta { get; set; }

    [Column("id_producto")]
    public int? IdProducto { get; set; }

    [Column("cantidad")]
    public int? Cantidad { get; set; }

    [Column("precio_unitario", TypeName = "decimal(10, 2)")]
    public decimal? PrecioUnitario { get; set; }

    [Column("subtotal", TypeName = "decimal(10, 2)")]
    public decimal? Subtotal { get; set; }

    [ForeignKey("IdProducto")]
    [InverseProperty("DetalleVenta")]
    public virtual Producto? IdProductoNavigation { get; set; }

    [ForeignKey("IdVenta")]
    [InverseProperty("DetalleVenta")]
    public virtual Ventum? IdVentaNavigation { get; set; }
}
