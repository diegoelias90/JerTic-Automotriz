package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.config.ApiConfig;
import sv.jertic.tecnico.service.ApiService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Panel de Perfil — El técnico ve y edita sus datos personales.
 */
public class PerfilController {

    @FXML private Label     lblIdUsuario;
    @FXML private Label     lblIdTecnico;
    @FXML private Label     lblRol;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private Label     lblEspecialidad;
    @FXML private Label     lblEstado;
    @FXML private Label     lblInfo;

    @FXML
    public void initialize() {
        lblIdUsuario.setText("ID Usuario: " + ApiConfig.ID_USUARIO);
        lblIdTecnico.setText("ID Técnico: " + ApiConfig.ID_TECNICO);
        lblRol.setText("Rol: " + ApiConfig.ROL);
        cargarPerfil();
    }

    @FXML
    public void cargarPerfil() {
        lblInfo.setText("Cargando perfil...");

        new Thread(() -> {
            try {
                String json = ApiService.get("/usuario/" + ApiConfig.ID_USUARIO);
                JsonObject u = JsonParser.parseString(json).getAsJsonObject();

                // Resolver especialidad si existe endpoint
                String especialidad = "—";
                try {
                    String jsonTec = ApiService.get("/usuariotecnico/" + ApiConfig.ID_TECNICO);
                    JsonObject t = JsonParser.parseString(jsonTec).getAsJsonObject();
                    int idEsp = t.has("idEspecialidad") ? t.get("idEspecialidad").getAsInt() : -1;
                    String jsonEsp = ApiService.get("/especialidadtecnico/" + idEsp);
                    JsonObject esp = JsonParser.parseString(jsonEsp).getAsJsonObject();
                    especialidad = esp.has("nombre") ? esp.get("nombre").getAsString() : "—";
                } catch (Exception ignored) {}

                final String esp = especialidad;
                final String nombre    = u.has("nombre")    ? u.get("nombre").getAsString()    : "";
                final String apellido  = u.has("apellido")  ? u.get("apellido").getAsString()  : "";
                final String correo    = u.has("correo")    ? u.get("correo").getAsString()     : "";
                final String telefono  = u.has("telefono")  ? u.get("telefono").getAsString()   : "";
                int estId = u.has("idEstado") ? u.get("idEstado").getAsInt() : -1;
                final String estado = switch (estId) {
                    case 1 -> "Activo";
                    case 2 -> "Inactivo";
                    default -> "Desconocido";
                };

                Platform.runLater(() -> {
                    txtNombre.setText(nombre);
                    txtApellido.setText(apellido);
                    txtCorreo.setText(correo);
                    txtTelefono.setText(telefono);
                    lblEspecialidad.setText("Especialidad: " + esp);
                    lblEstado.setText("Estado: " + estado);
                    lblInfo.setText("Perfil cargado.");
                });

            } catch (Exception ex) {
                Platform.runLater(() -> lblInfo.setText("Error al cargar perfil: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    private void guardarCambios() {
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String correo    = txtCorreo.getText().trim();
        String telefono  = txtTelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Nombre, Apellido y Correo son obligatorios.", ButtonType.OK).showAndWait();
            return;
        }

        new Thread(() -> {
            try {
                String json = String.format(
                    "{\"idUsuario\":%d,\"nombre\":\"%s\",\"apellido\":\"%s\"," +
                    "\"correo\":\"%s\",\"telefono\":\"%s\"}",
                    ApiConfig.ID_USUARIO,
                    nombre.replace("\"", "\\\""),
                    apellido.replace("\"", "\\\""),
                    correo,
                    telefono.replace("\"", "\\\"")
                );
                ApiService.put("/usuario/" + ApiConfig.ID_USUARIO, json);

                // Actualizar nombre en ApiConfig
                ApiConfig.NOMBRE = nombre + " " + apellido;

                Platform.runLater(() ->
                    new Alert(Alert.AlertType.INFORMATION, "Perfil actualizado.", ButtonType.OK).showAndWait()
                );
            } catch (Exception ex) {
                Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR, "Error al guardar:\n" + ex.getMessage(), ButtonType.OK).showAndWait()
                );
            }
        }).start();
    }
}
