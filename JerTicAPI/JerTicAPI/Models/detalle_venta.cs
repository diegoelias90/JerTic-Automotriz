namespace JerTicAPI.Models
{
    public class detalle_venta
    {
        public int id {  get; set; }
        public int id_venta { get; set; }
        public int id_producto { get; set; }
        public int cantidad { get; set; }
        public decimal precio_unitario { get; set; }
        public decimal subtotal { get; set; }
    }
}
