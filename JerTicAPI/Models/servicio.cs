using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("servicio")]
public partial class Servicio
{
    [Key]
    [Column("id_servicio")]
    public int IdServicio { get; set; }

    [Column("id_cita")]
    public int? IdCita { get; set; }

    [Column("id_tecnico")]
    public int? IdTecnico { get; set; }

    [Column("descripcion")]
    public string? Descripcion { get; set; }

    [Column("costo", TypeName = "decimal(10, 2)")]
    public decimal? Costo { get; set; }

    [Column("fecha_inicio", TypeName = "datetime")]
    public DateTime? FechaInicio { get; set; }

    [Column("fecha_fin", TypeName = "datetime")]
    public DateTime? FechaFin { get; set; }

    [Column("id_estado")]
    public int? IdEstado { get; set; }

    [Column("id_tipo_servicio")]
    public int? IdTipoServicio { get; set; }

    [InverseProperty("IdServicioNavigation")]
    public virtual ICollection<HistorialServicio> HistorialServicios { get; set; } = new List<HistorialServicio>();

    [ForeignKey("IdCita")]
    [InverseProperty("Servicios")]
    public virtual Citum? IdCitaNavigation { get; set; }

    [ForeignKey("IdEstado")]
    [InverseProperty("Servicios")]
    public virtual EstadoGeneral? IdEstadoNavigation { get; set; }

    [ForeignKey("IdTecnico")]
    [InverseProperty("Servicios")]
    public virtual UsuarioTecnico? IdTecnicoNavigation { get; set; }

    [ForeignKey("IdTipoServicio")]
    [InverseProperty("Servicios")]
    public virtual TipoServicio? IdTipoServicioNavigation { get; set; }
}
