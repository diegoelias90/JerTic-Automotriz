package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.config.ApiConfig;
import sv.jertic.tecnico.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     txtCorreo;
    @FXML private PasswordField txtToken;
    @FXML private Label         lblMensaje;
    @FXML private Button        btnIngresar;

    @FXML
    public void initialize() {
        txtToken.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) handleLogin();
        });
    }

    @FXML
    private void handleLogin() {
        String correo = txtCorreo.getText().trim();
        String token  = txtToken.getText().trim();

        if (correo.isEmpty() || token.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }

        btnIngresar.setDisable(true);
        btnIngresar.setText("Verificando...");
        lblMensaje.setStyle("-fx-text-fill: #B4B4C8;");
        lblMensaje.setText("Conectando con el servidor...");

        new Thread(() -> {
            boolean ok = AuthService.login(correo, token);
            String rolObtenido = ApiConfig.ROL;

            Platform.runLater(() -> {
                btnIngresar.setDisable(false);
                btnIngresar.setText("Ingresar");

                if (!ok) {
                    String rolNorm = AuthService.normalizarTexto(rolObtenido);
                    if (!rolObtenido.isEmpty() && !rolNorm.equals("tecnico")) {
                        AuthService.limpiarSesion();
                        mostrarError("Acceso denegado. Solo tecnicos pueden ingresar.");
                    } else {
                        mostrarError("Correo o token incorrectos.");
                    }
                    txtToken.clear();
                    return;
                }

                abrirPrincipal();
            });
        }).start();
    }

    private void abrirPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sv/jertic/tecnico/views/main.fxml")
            );
            Scene scene = new Scene(loader.load(), 1250, 750);
            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            stage.setTitle("Jertic Automotriz - Panel Tecnico");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
        } catch (Exception e) {
            mostrarError("Error al abrir la ventana principal.");
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #FF6464;");
        lblMensaje.setText(mensaje);
    }
}