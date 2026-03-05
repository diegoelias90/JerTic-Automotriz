using System.ComponentModel.DataAnnotations;

namespace JerTicAPI.Models
{
    public class productos
    {
        public int id { get; set; }
        public string nombre { get; set; }
        public string descripcion { get; set; }
        public decimal precio { get; set; }

        [Range(0, int.MaxValue, ErrorMessage = "No se puede ingresar un dato negativo.")]
        public int stock { get; set; }
        public DateTime fecha_creacion { get; set; }
        public int id_tipo_producto { get; set; }
    }
}
