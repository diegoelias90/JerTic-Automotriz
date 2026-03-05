package sv.jertic.tecnico.config;

/**
 * Configuración global de la API.
 * Se llena al hacer login y se usa en toda la app.
 */
public class ApiConfig {
    public static final String BASE_URL = "http://localhost:5205/api";

    public static String JWT_TOKEN = "";   // Bearer token devuelto por /Auth/login
    public static String ROL       = "";   // "Tecnico"
    public static String NOMBRE    = "";   // Nombre completo del técnico logueado
    public static String CORREO    = "";   // Correo usado en el login

    // IDs del técnico — se resuelven tras el login con llamadas adicionales
    public static int ID_USUARIO = -1;  // usuario.id_usuario
    public static int ID_TECNICO = -1;  // usuario_tecnico.id_tecnico
}
