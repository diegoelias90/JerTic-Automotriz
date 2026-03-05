package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.config.ApiConfig;
import sv.jertic.tecnico.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controlador de la ventana principal.
 * Gestiona el sidebar y carga dinámicamente las vistas en el contentPane.
 */
public class MainController {

    @FXML private Label    lblNombre;
    @FXML private Label    lblRol;
    @FXML private AnchorPane contentPane;

    // Botones del sidebar
    @FXML private Button btnCitas;
    @FXML private Button btnServicios;
    @FXML private Button btnHistorial;
    @FXML private Button btnProductos;
    @FXML private Button btnVentas;
    @FXML private Button btnPerfil;

    private Button btnActivo = null;

    // Estilos para botón activo/inactivo del sidebar
    private static final String ESTILO_NORMAL =
        "-fx-background-color: transparent;" +
        "-fx-text-fill: #B4B4C8;" +
        "-fx-font-size: 13;" +
        "-fx-font-weight: normal;" +
        "-fx-alignment: CENTER-LEFT;" +
        "-fx-padding: 0 0 0 20;" +
        "-fx-cursor: hand;";

    private static final String ESTILO_ACTIVO =
        "-fx-background-color: #24201A;" +
        "-fx-text-fill: #FFA500;" +
        "-fx-font-size: 13;" +
        "-fx-font-weight: bold;" +
        "-fx-alignment: CENTER-LEFT;" +
        "-fx-padding: 0 0 0 17;" +
        "-fx-border-color: transparent transparent transparent #FFA500;" +
        "-fx-border-width: 0 0 0 3;" +
        "-fx-cursor: hand;";

    @FXML
    public void initialize() {
        // Datos del técnico logueado
        lblNombre.setText(ApiConfig.NOMBRE);
        lblRol.setText(ApiConfig.ROL);

        // Aplicar estilo normal a todos los botones
        for (Button btn : new Button[]{btnCitas, btnServicios, btnHistorial, btnProductos, btnVentas, btnPerfil}) {
            btn.setStyle(ESTILO_NORMAL);
            btn.setPrefSize(200, 42);
            btn.setMaxWidth(Double.MAX_VALUE);
        }

        // Hover — replicar comportamiento del Swing admin
        for (Button btn : new Button[]{btnCitas, btnServicios, btnHistorial, btnProductos, btnVentas, btnPerfil}) {
            btn.setOnMouseEntered(e -> {
                if (btn != btnActivo)
                    btn.setStyle(ESTILO_NORMAL.replace("transparent", "#1E1E2E"));
            });
            btn.setOnMouseExited(e -> {
                if (btn != btnActivo)
                    btn.setStyle(ESTILO_NORMAL);
            });
        }

        // Cargar vista inicial
        activarBoton(btnCitas);
        cargarVista("citas.fxml");
    }

    // ── Acciones del sidebar ──────────────────────────────────────────────────

    @FXML private void mostrarCitas()     { activarBoton(btnCitas);     cargarVista("citas.fxml"); }
    @FXML private void mostrarServicios() { activarBoton(btnServicios); cargarVista("servicios.fxml"); }
    @FXML private void mostrarHistorial() { activarBoton(btnHistorial); cargarVista("historial.fxml"); }
    @FXML private void mostrarProductos() { activarBoton(btnProductos); cargarVista("productos.fxml"); }
    @FXML private void mostrarVentas()    { activarBoton(btnVentas);    cargarVista("ventas.fxml"); }
    @FXML private void mostrarPerfil()    { activarBoton(btnPerfil);    cargarVista("perfil.fxml"); }

    @FXML
    private void cerrarSesion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Cerrar sesión?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                AuthService.limpiarSesion();
                try {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/sv/jertic/tecnico/views/login.fxml")
                    );
                    Stage stage = (Stage) contentPane.getScene().getWindow();
                    stage.setScene(new Scene(loader.load(), 440, 420));
                    stage.setTitle("Jertic Automotriz — Acceso Técnico");
                    stage.setResizable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void activarBoton(Button btn) {
        if (btnActivo != null) btnActivo.setStyle(ESTILO_NORMAL);
        btn.setStyle(ESTILO_ACTIVO);
        btnActivo = btn;
    }

    private void cargarVista(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/sv/jertic/tecnico/views/" + fxmlFile)
            );
            Parent vista = loader.load();
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            contentPane.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
            Label err = new Label("Error al cargar la vista: " + fxmlFile);
            err.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            contentPane.getChildren().setAll(err);
        }
    }
}
