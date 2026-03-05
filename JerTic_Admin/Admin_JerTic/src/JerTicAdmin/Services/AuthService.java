/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JerTicAdmin.Services;
import JerTicAdmin.Config.ApiConfig;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author djelo
 */
public class AuthService {
    
     public static boolean login(String correo, String token) {
        try {
            URL url = new URL(ApiConfig.BASE_URL + "/Auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            //Lo mismo de postman joder
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            //Body con correo y token
            String body = String.format(
                "{\"correo\":\"%s\",\"token\":\"%s\"}", correo, token);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes("utf-8"));
            }

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // Guardamos el JWT y los datos del usuario
                JsonObject resp = new Gson().fromJson(sb.toString(), JsonObject.class);
                ApiConfig.JWT_TOKEN = resp.get("token").getAsString(); //Aquí se guarda el token que devuelve jwt para verificar al usuario 
                ApiConfig.ROL = resp.get("rol").getAsString();
                ApiConfig.NOMBRE = resp.get("nombre").getAsString();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
