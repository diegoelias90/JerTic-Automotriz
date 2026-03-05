package sv.jertic.tecnico.service;

import sv.jertic.tecnico.config.ApiConfig;
import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.Base64;

public class AuthService {

    public static boolean login(String correo, String token) {

        // Paso 1: POST /Auth/login
        try {
            URL url = new URL(ApiConfig.BASE_URL + "/Auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = String.format("{\"correo\":\"%s\",\"token\":\"%s\"}", correo, token);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes("utf-8"));
            }

            if (conn.getResponseCode() != 200) return false;

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JsonObject resp = new Gson().fromJson(sb.toString(), JsonObject.class);
            ApiConfig.JWT_TOKEN = resp.get("token").getAsString();
            ApiConfig.ROL       = resp.get("rol").getAsString();
            ApiConfig.NOMBRE    = resp.get("nombre").getAsString();
            ApiConfig.CORREO    = correo;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Paso 2: verificar rol (acepta "Tecnico", "Técnico", etc.)
        if (!normalizarTexto(ApiConfig.ROL).equals("tecnico")) {
            return false;
        }

        // Paso 3: leer ID_USUARIO del payload JWT
        // El claim NameIdentifier contiene usuario.id_usuario
        try {
            String[] partes = ApiConfig.JWT_TOKEN.split("\\.");
            String payload = partes[1];
            int pad = 4 - payload.length() % 4;
            if (pad < 4) payload += "=".repeat(pad);
            String json = new String(Base64.getUrlDecoder().decode(payload), "utf-8");
            JsonObject claims = JsonParser.parseString(json).getAsJsonObject();

            // .NET serializa NameIdentifier con esta URI completa
            String claimUri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";
            if (claims.has(claimUri)) {
                ApiConfig.ID_USUARIO = claims.get(claimUri).getAsInt();
            } else if (claims.has("nameid")) {
                ApiConfig.ID_USUARIO = claims.get("nameid").getAsInt();
            } else if (claims.has("sub")) {
                ApiConfig.ID_USUARIO = claims.get("sub").getAsInt();
            }
        } catch (Exception e) {
            System.out.println("Advertencia JWT: " + e.getMessage());
        }

        // Paso 4: resolver ID_TECNICO desde /usuariotecnico
        // IMPORTANTE: id_tecnico != id_usuario
        // usuario_tecnico tiene su propia PK id_tecnico con FK a usuario.id_usuario
        // Servicio.IdTecnico referencia usuario_tecnico.id_tecnico
        if (ApiConfig.ID_USUARIO != -1) {
            try {
                String jsonTecs = ApiService.get("/usuariotecnico");
                JsonArray tecs = JsonParser.parseString(jsonTecs).getAsJsonArray();
                for (JsonElement el : tecs) {
                    JsonObject t = el.getAsJsonObject();
                    if (!t.has("idUsuario") || t.get("idUsuario").isJsonNull()) continue;
                    if (t.get("idUsuario").getAsInt() == ApiConfig.ID_USUARIO) {
                        // idTecnico es la PK de usuario_tecnico — el que usa Servicio.id_tecnico
                        if (t.has("idTecnico") && !t.get("idTecnico").isJsonNull()) {
                            ApiConfig.ID_TECNICO = t.get("idTecnico").getAsInt();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Advertencia usuariotecnico: " + e.getMessage());
            }
        }

        // Si no se pudo resolver ID_TECNICO, el login igual funciona
        // pero las vistas de servicios/citas quedarán vacías
        System.out.println("Login OK — ID_USUARIO=" + ApiConfig.ID_USUARIO
                + " ID_TECNICO=" + ApiConfig.ID_TECNICO);

        return true;
    }

    public static String normalizarTexto(String texto) {
        if (texto == null) return "";
        String s = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        return s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

    public static void limpiarSesion() {
        ApiConfig.JWT_TOKEN  = "";
        ApiConfig.ROL        = "";
        ApiConfig.NOMBRE     = "";
        ApiConfig.CORREO     = "";
        ApiConfig.ID_USUARIO = -1;
        ApiConfig.ID_TECNICO = -1;
    }
}