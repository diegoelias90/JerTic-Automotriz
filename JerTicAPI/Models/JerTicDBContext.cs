using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace JerTicAPI.Models;

public partial class JerTicDBContext : DbContext
{
    public JerTicDBContext()
    {
    }

    public JerTicDBContext(DbContextOptions<JerTicDBContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Citum> Cita { get; set; }

    public virtual DbSet<DetalleVentum> DetalleVenta { get; set; }

    public virtual DbSet<EspecialidadTecnico> EspecialidadTecnicos { get; set; }

    public virtual DbSet<EstadoGeneral> EstadoGenerals { get; set; }

    public virtual DbSet<HistorialServicio> HistorialServicios { get; set; }

    public virtual DbSet<Producto> Productos { get; set; }

    public virtual DbSet<Servicio> Servicios { get; set; }

    public virtual DbSet<TipoProducto> TipoProductos { get; set; }

    public virtual DbSet<TipoServicio> TipoServicios { get; set; }

    public virtual DbSet<TipoUsuario> TipoUsuarios { get; set; }

    public virtual DbSet<Usuario> Usuarios { get; set; }

    public virtual DbSet<UsuarioCliente> UsuarioClientes { get; set; }

    public virtual DbSet<UsuarioTecnico> UsuarioTecnicos { get; set; }

    public virtual DbSet<Ventum> Venta { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            optionsBuilder.UseSqlServer("Server=DIEGOLAPTOP\\SQL2022;Database=jertic_automotriz;Trusted_Connection=True;TrustServerCertificate=True;");
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Citum>(entity =>
        {
            entity.HasKey(e => e.IdCita).HasName("PK__cita__6AEC3C093F67F765");

            entity.HasOne(d => d.IdClienteNavigation).WithMany(p => p.Cita).HasConstraintName("FK__cita__id_cliente__5812160E");

            entity.HasOne(d => d.IdEstadoNavigation).WithMany(p => p.Cita).HasConstraintName("FK__cita__id_estado__59063A47");
        });

        modelBuilder.Entity<DetalleVentum>(entity =>
        {
            entity.HasKey(e => e.IdDetalle).HasName("PK__detalle___4F1332DE765B5BEB");

            entity.ToTable("detalle_venta", tb =>
            {
                tb.HasTrigger("trg_validar_y_restar_stock");
                tb.UseSqlOutputClause(false);
            });

            entity.HasOne(d => d.IdProductoNavigation).WithMany(p => p.DetalleVenta).HasConstraintName("FK__detalle_v__id_pr__68487DD7");

            entity.HasOne(d => d.IdVentaNavigation).WithMany(p => p.DetalleVenta).HasConstraintName("FK__detalle_v__id_ve__6754599E");
        });

        modelBuilder.Entity<EspecialidadTecnico>(entity =>
        {
            entity.HasKey(e => e.IdEspecialidad).HasName("PK__especial__C1D1376384000A88");
        });

        modelBuilder.Entity<EstadoGeneral>(entity =>
        {
            entity.HasKey(e => e.IdEstado).HasName("PK__estado_g__86989FB2E4AE042D");
        });

        modelBuilder.Entity<HistorialServicio>(entity =>
        {
            entity.HasKey(e => e.IdHistorial).HasName("PK__historia__76E6C5026B3C9010");

            entity.HasOne(d => d.IdServicioNavigation).WithMany(p => p.HistorialServicios).HasConstraintName("FK__historial__id_se__619B8048");
        });

        modelBuilder.Entity<Producto>(entity =>
        {
            entity.HasKey(e => e.IdProducto).HasName("PK__producto__FF341C0DC046D71E");

            entity.Property(e => e.FechaCreacion).HasDefaultValueSql("(getdate())");

            entity.HasOne(d => d.IdTipoProductoNavigation).WithMany(p => p.Productos).HasConstraintName("FK__productos__id_ti__5535A963");
        });

        modelBuilder.Entity<Servicio>(entity =>
        {
            entity.HasKey(e => e.IdServicio).HasName("PK__servicio__6FD07FDCA143505C");

            entity.HasOne(d => d.IdCitaNavigation).WithMany(p => p.Servicios).HasConstraintName("FK__servicio__id_cit__5BE2A6F2");

            entity.HasOne(d => d.IdEstadoNavigation).WithMany(p => p.Servicios).HasConstraintName("FK__servicio__id_est__5DCAEF64");

            entity.HasOne(d => d.IdTecnicoNavigation).WithMany(p => p.Servicios).HasConstraintName("FK__servicio__id_tec__5CD6CB2B");

            entity.HasOne(d => d.IdTipoServicioNavigation).WithMany(p => p.Servicios).HasConstraintName("FK__servicio__id_tip__5EBF139D");
        });

        modelBuilder.Entity<TipoProducto>(entity =>
        {
            entity.HasKey(e => e.IdTipoProducto).HasName("PK__tipo_pro__F5E0BFB88D71ECD6");
        });

        modelBuilder.Entity<TipoServicio>(entity =>
        {
            entity.HasKey(e => e.IdTipoServicio).HasName("PK__tipo_ser__4227AB8ED2C5EBA3");
        });

        modelBuilder.Entity<TipoUsuario>(entity =>
        {
            entity.HasKey(e => e.IdTipoUsuario).HasName("PK__tipo_usu__B17D78C88DC07141");
        });

        modelBuilder.Entity<Usuario>(entity =>
        {
            entity.HasKey(e => e.IdUsuario).HasName("PK__usuario__4E3E04ADC843CD87");

            entity.Property(e => e.FechaRegistro).HasDefaultValueSql("(getdate())");

            entity.HasOne(d => d.IdEstadoNavigation).WithMany(p => p.Usuarios).HasConstraintName("FK__usuario__id_esta__48CFD27E");

            entity.HasOne(d => d.IdTipoUsuarioNavigation).WithMany(p => p.Usuarios).HasConstraintName("FK__usuario__id_tipo__47DBAE45");
        });

        modelBuilder.Entity<UsuarioCliente>(entity =>
        {
            entity.HasKey(e => e.IdCliente).HasName("PK__usuario___677F38F5C2F8866E");

            entity.HasOne(d => d.IdUsuarioNavigation).WithOne(p => p.UsuarioCliente).HasConstraintName("FK__usuario_c__id_us__4CA06362");
        });

        modelBuilder.Entity<UsuarioTecnico>(entity =>
        {
            entity.HasKey(e => e.IdTecnico).HasName("PK__usuario___D5509737B8C27238");

            entity.HasOne(d => d.IdEspecialidadNavigation).WithMany(p => p.UsuarioTecnicos).HasConstraintName("FK__usuario_t__id_es__5165187F");

            entity.HasOne(d => d.IdUsuarioNavigation).WithOne(p => p.UsuarioTecnico).HasConstraintName("FK__usuario_t__id_us__5070F446");
        });

        modelBuilder.Entity<Ventum>(entity =>
        {
            entity.HasKey(e => e.IdVenta).HasName("PK__venta__459533BFA3A56440");

            entity.HasOne(d => d.IdClienteNavigation).WithMany(p => p.Venta).HasConstraintName("FK__venta__id_client__6477ECF3");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
