package sv.jertic.tecnico.service;

import sv.jertic.tecnico.config.ApiConfig;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Servicio HTTP central.
 * Maneja GET, POST, PUT, DELETE con el JWT en el header Authorization.
 * NOTA: HttpURLConnection de Java no soporta PATCH — usar PUT/POST en su lugar.
 */
public class ApiService {

    public static String get(String endpoint) throws Exception {
        HttpURLConnection conn = abrir("GET", endpoint);
        return leer(conn);
    }

    public static String post(String endpoint, String json) throws Exception {
        return conCuerpo("POST", endpoint, json);
    }

    public static String put(String endpoint, String json) throws Exception {
        return conCuerpo("PUT", endpoint, json);
    }

    public static String delete(String endpoint) throws Exception {
        HttpURLConnection conn = abrir("DELETE", endpoint);
        return leer(conn);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private static HttpURLConnection abrir(String metodo, String endpoint) throws Exception {
        URL url = new URL(ApiConfig.BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(metodo);
        conn.setRequestProperty("Authorization", "Bearer " + ApiConfig.JWT_TOKEN);
        conn.setRequestProperty("Content-Type",  "application/json");
        conn.setRequestProperty("Accept",        "application/json");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(15000);
        return conn;
    }

    private static String conCuerpo(String metodo, String endpoint, String json) throws Exception {
        HttpURLConnection conn = abrir(metodo, endpoint);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("utf-8"));
        }
        return leer(conn);
    }

    private static String leer(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        if (status == 204) return ""; // No Content — éxito sin cuerpo

        InputStream is = (status >= 200 && status < 300)
            ? conn.getInputStream()
            : conn.getErrorStream();

        if (is == null) throw new Exception("Error HTTP " + status + " sin detalle.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();

        if (status < 200 || status >= 300)
            throw new Exception("Error HTTP " + status + ": " + sb);

        return sb.toString();
    }
}
