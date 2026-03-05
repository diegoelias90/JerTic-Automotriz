package sv.jertic.tecnico;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JavaFX — Panel del Técnico.
 * Carga el login.fxml como ventana inicial.
 */
public class JerTicApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/sv/jertic/tecnico/views/login.fxml")
        );
        Scene scene = new Scene(loader.load(), 440, 420);
        stage.setTitle("Jertic Automotriz — Acceso Técnico");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
