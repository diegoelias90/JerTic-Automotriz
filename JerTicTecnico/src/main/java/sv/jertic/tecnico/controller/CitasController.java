package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.config.ApiConfig;
import sv.jertic.tecnico.service.ApiService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CitasController {

    @FXML private TableView<ObservableList<String>>           tablaCitas;
    @FXML private TableColumn<ObservableList<String>, String> colIdCita;
    @FXML private TableColumn<ObservableList<String>, String> colCliente;
    @FXML private TableColumn<ObservableList<String>, String> colFecha;
    @FXML private TableColumn<ObservableList<String>, String> colHora;
    @FXML private TableColumn<ObservableList<String>, String> colMotivo;
    @FXML private TableColumn<ObservableList<String>, String> colEstado;

    @FXML private Label lblIdCita;
    @FXML private Label lblCliente;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblMotivo;
    @FXML private Label lblEstado;
    @FXML private Label lblInfo;

    @FXML
    public void initialize() {
        colIdCita.setCellValueFactory( d -> new SimpleStringProperty(d.getValue().get(0)));
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colFecha.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue().get(2)));
        colHora.setCellValueFactory(   d -> new SimpleStringProperty(d.getValue().get(3)));
        colMotivo.setCellValueFactory( d -> new SimpleStringProperty(d.getValue().get(4)));
        colEstado.setCellValueFactory( d -> new SimpleStringProperty(d.getValue().get(5)));

        tablaCitas.setRowFactory(tv -> new TableRow<ObservableList<String>>() {
            @Override
            protected void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                switch (item.get(5)) {
                    case "Programada" -> setStyle("-fx-background-color: #C8E6FF;");
                    case "En proceso" -> setStyle("-fx-background-color: #FFF5B4;");
                    case "Finalizado" -> setStyle("-fx-background-color: #C8FFC8;");
                    case "Cancelado"  -> setStyle("-fx-background-color: #FFC8C8;");
                    default           -> setStyle("");
                }
            }
        });

        tablaCitas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> { if (nuevo != null) llenarDetalle(nuevo); }
        );

        cargarCitas();
    }

    @FXML
    public void cargarCitas() {
        lblInfo.setText("Cargando...");
        tablaCitas.getItems().clear();
        limpiarDetalle();

        new Thread(() -> {
            try {
                // Obtener servicios y filtrar los del tecnico actual
                String jsonServicios = ApiService.get("/servicio");
                JsonArray servicios = JsonParser.parseString(jsonServicios).getAsJsonArray();

                java.util.Set<Integer> misCitasIds = new java.util.HashSet<>();
                for (JsonElement el : servicios) {
                    JsonObject s = el.getAsJsonObject();
                    if (!s.has("idTecnico") || s.get("idTecnico").isJsonNull()) continue;
                    if (s.get("idTecnico").getAsInt() != ApiConfig.ID_TECNICO) continue;
                    if (s.has("idCita") && !s.get("idCita").isJsonNull()) {
                        misCitasIds.add(s.get("idCita").getAsInt());
                    }
                }

                String jsonCitas = ApiService.get("/cita");
                JsonArray citas = JsonParser.parseString(jsonCitas).getAsJsonArray();
                ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();

                for (JsonElement el : citas) {
                    JsonObject c = el.getAsJsonObject();
                    if (!c.has("idCita") || c.get("idCita").isJsonNull()) continue;

                    int idCita = c.get("idCita").getAsInt();
                    if (!misCitasIds.contains(idCita)) continue;

                    String estado = "N/A";
                    if (c.has("idEstado") && !c.get("idEstado").isJsonNull()) {
                        estado = switch (c.get("idEstado").getAsInt()) {
                            case 3 -> "Pendiente";
                            case 4 -> "Programada";
                            case 5 -> "En proceso";
                            case 6 -> "Finalizado";
                            case 7 -> "Cancelado";
                            default -> "N/A";
                        };
                    }

                    String fecha = strSafe(c, "fecha");
                    if (fecha.contains("T")) fecha = fecha.substring(0, 10);

                    rows.add(FXCollections.observableArrayList(
                            String.valueOf(idCita),
                            strInt(c, "idCliente"),
                            fecha,
                            strSafe(c, "hora"),
                            strSafe(c, "motivo"),
                            estado
                    ));
                }

                final int total = rows.size();
                Platform.runLater(() -> {
                    tablaCitas.setItems(rows);
                    lblInfo.setText("Citas asignadas: " + total);
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblInfo.setText("Error al cargar citas.");
                    new Alert(Alert.AlertType.ERROR,
                            "Error:\n" + ex.getMessage(), ButtonType.OK).showAndWait();
                });
            }
        }).start();
    }

    private void llenarDetalle(ObservableList<String> fila) {
        lblIdCita.setText( "ID Cita: "    + fila.get(0));
        lblCliente.setText("Cliente ID: " + fila.get(1));
        lblFecha.setText(  "Fecha: "      + fila.get(2));
        lblHora.setText(   "Hora: "       + fila.get(3));
        lblMotivo.setText( "Motivo: "     + fila.get(4));
        lblEstado.setText( "Estado: "     + fila.get(5));
    }

    private void limpiarDetalle() {
        lblIdCita.setText( "ID Cita: —");
        lblCliente.setText("Cliente ID: —");
        lblFecha.setText(  "Fecha: —");
        lblHora.setText(   "Hora: —");
        lblMotivo.setText( "Motivo: —");
        lblEstado.setText( "Estado: —");
    }

    private String strSafe(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return "—";
        return obj.get(key).getAsString();
    }

    private String strInt(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return "—";
        return String.valueOf(obj.get(key).getAsInt());
    }
}