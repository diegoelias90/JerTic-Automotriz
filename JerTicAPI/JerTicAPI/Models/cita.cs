namespace JerTicAPI.Models
{
    public class cita
    {
        public int Id { get; set; }
        public int id_cliente { get; set; }
        public DateTime fecha { get; set; }
        public TimeOnly hora { get; set; }
        public string motivo { get; set; }
        public int id_estado { get; set; }
    }
}
