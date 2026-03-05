module sv.jertic.tecnico {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // Abre los paquetes al FXML loader para que pueda instanciar controladores
    opens sv.jertic.tecnico to javafx.fxml;
    opens sv.jertic.tecnico.controller to javafx.fxml;
    opens sv.jertic.tecnico.config to javafx.fxml;
    opens sv.jertic.tecnico.service to javafx.fxml;

    exports sv.jertic.tecnico;
}
