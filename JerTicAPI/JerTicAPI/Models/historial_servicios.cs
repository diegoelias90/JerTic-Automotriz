namespace JerTicAPI.Models
{
    public class historial_servicios
    {
        public int id {  get; set; }
        public int id_servicio { get; set; }
        public string descripcion { get; set; }
        public DateTime fecha_evento { get; set; } = DateTime.Now;
    }
}
