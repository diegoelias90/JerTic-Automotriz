using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

[Table("cita")]
public partial class Citum
{
    [Key]
    [Column("id_cita")]
    public int IdCita { get; set; }

    [Column("id_cliente")]
    public int? IdCliente { get; set; }

    [Column("fecha")]
    public DateOnly? Fecha { get; set; }

    [Column("hora")]
    public TimeOnly? Hora { get; set; }

    [Column("motivo")]
    public string? Motivo { get; set; }

    [Column("id_estado")]
    public int? IdEstado { get; set; }

    [ForeignKey("IdCliente")]
    [InverseProperty("Cita")]
    public virtual UsuarioCliente? IdClienteNavigation { get; set; }

    [ForeignKey("IdEstado")]
    [InverseProperty("Cita")]
    public virtual EstadoGeneral? IdEstadoNavigation { get; set; }

    [InverseProperty("IdCitaNavigation")]
    public virtual ICollection<Servicio> Servicios { get; set; } = new List<Servicio>();
}
