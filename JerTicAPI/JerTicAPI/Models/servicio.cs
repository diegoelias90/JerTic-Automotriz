namespace JerTicAPI.Models
{
    public class servicio
    {
        public int id {  get; set; }
        public int id_cita { get; set; }
        public int id_tecnico { get; set; }
        public string descripcion {  get; set; }
        public decimal costo { get; set; }
        public DateTime fecha_inicio { get; set; }
        public DateTime fecha_fin { get; set; }
        public int id_estado { get; set; }
        public int id_tipo_servicio { get; set; }
    }
}
