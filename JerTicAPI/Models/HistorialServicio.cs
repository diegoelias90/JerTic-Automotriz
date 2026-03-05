using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("historial_servicios")]
public partial class HistorialServicio
{
    [Key]
    [Column("id_historial")]
    public int IdHistorial { get; set; }

    [Column("id_servicio")]
    public int? IdServicio { get; set; }

    [Column("descripcion")]
    public string? Descripcion { get; set; }

    [Column("fecha_evento", TypeName = "datetime")]
    public DateTime? FechaEvento { get; set; }

    [ForeignKey("IdServicio")]
    [InverseProperty("HistorialServicios")]
    public virtual Servicio? IdServicioNavigation { get; set; }
}
