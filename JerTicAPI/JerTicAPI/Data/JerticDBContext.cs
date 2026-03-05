using JerTicAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Data
{
    public class JerticDBContext : DbContext
    {
        public JerticDBContext(DbContextOptions<JerticDBContext> options) : base(options) { }

        public DbSet<estado_general> estado_General { get; set; }
        public DbSet<tipo_usuario> tipo_Usuario { get; set; }
        public DbSet<tipo_producto> tipo_Producto { get; set; }
        public DbSet<tipo_servicio> tipo_Servicio { get; set; }
        public DbSet<especialidad_tecnico> especialidad_Tecnicos { get; set; }
        public DbSet<usuario> Usuario { get; set; }
        public DbSet<usuario_cliente> usuario_Cliente { get; set; }
        public DbSet<usuario_tecnico> usuario_Tecnico { get; set; }
        public DbSet<productos> Productos { get; set; }
        public DbSet<cita> Cita { get; set; }
        public DbSet<servicio> Servicio { get; set; }
        public DbSet<historial_servicios> historial_Servicio { get; set; }
        public DbSet<venta> Venta { get; set; }
        public DbSet<detalle_venta> detalle_Venta { get; set; }

    }
}
