using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("tipo_producto")]
[Index("Nombre", Name = "UQ__tipo_pro__72AFBCC6862470F3", IsUnique = true)]
public partial class TipoProducto
{
    [Key]
    [Column("id_tipo_producto")]
    public int IdTipoProducto { get; set; }

    [Column("nombre")]
    [StringLength(100)]
    public string? Nombre { get; set; }

    [InverseProperty("IdTipoProductoNavigation")]
    public virtual ICollection<Producto> Productos { get; set; } = new List<Producto>();
}
