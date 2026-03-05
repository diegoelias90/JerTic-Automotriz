namespace JerTicAPI.Models
{
    public class venta
    {
        public int id { get; set; }
        public int id_cliente { get; set; }
        public DateTime fecha { get; set; }
        public decimal total { get; set; }
        public string estado { get; set; } //Este es uno nuevo para ver el estado de la venta o entrega
    }
}
