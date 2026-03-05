package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.config.ApiConfig;
import sv.jertic.tecnico.service.ApiService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Locale;

public class ServiciosController {

    @FXML private TableView<ObservableList<String>>           tablaServicios;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colCita;
    @FXML private TableColumn<ObservableList<String>, String> colDescripcion;
    @FXML private TableColumn<ObservableList<String>, String> colCosto;
    @FXML private TableColumn<ObservableList<String>, String> colEstado;
    @FXML private TableColumn<ObservableList<String>, String> colTipo;
    @FXML private TableColumn<ObservableList<String>, String> colFechaInicio;

    @FXML private Label            lblServicioId;
    @FXML private TextArea         txtDescripcion;
    @FXML private TextField        txtCosto;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label            lblInfo;

    private int idServicioSeleccionado = -1;

    private static final String[] ESTADOS    = {"Pendiente","Programada","En proceso","Finalizado","Cancelado"};
    private static final int[]    ID_ESTADOS = {3, 4, 5, 6, 7};

    @FXML
    public void initialize() {
        colId.setCellValueFactory(         d -> new SimpleStringProperty(d.getValue().get(0)));
        colCita.setCellValueFactory(       d -> new SimpleStringProperty(d.getValue().get(1)));
        colDescripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colCosto.setCellValueFactory(      d -> new SimpleStringProperty(d.getValue().get(3)));
        colEstado.setCellValueFactory(     d -> new SimpleStringProperty(d.getValue().get(4)));
        colTipo.setCellValueFactory(       d -> new SimpleStringProperty(d.getValue().get(5)));
        colFechaInicio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(6)));

        tablaServicios.setRowFactory(tv -> new TableRow<ObservableList<String>>() {
            @Override
            protected void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                switch (item.get(4)) {
                    case "En proceso" -> setStyle("-fx-background-color: #FFF5B4;");
                    case "Finalizado" -> setStyle("-fx-background-color: #C8FFC8;");
                    case "Cancelado"  -> setStyle("-fx-background-color: #FFC8C8;");
                    default           -> setStyle("");
                }
            }
        });

        cmbEstado.getItems().addAll(ESTADOS);

        tablaServicios.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> { if (nuevo != null) llenarFormulario(nuevo); }
        );

        cargarServicios();
    }

    @FXML
    public void cargarServicios() {
        lblInfo.setText("Cargando...");
        tablaServicios.getItems().clear();
        limpiarFormulario();

        new Thread(() -> {
            try {
                String json = ApiService.get("/servicio");
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();

                for (JsonElement el : arr) {
                    JsonObject s = el.getAsJsonObject();
                    if (!s.has("idTecnico") || s.get("idTecnico").isJsonNull()) continue;
                    if (s.get("idTecnico").getAsInt() != ApiConfig.ID_TECNICO) continue;

                    String estado = "N/A";
                    if (s.has("idEstado") && !s.get("idEstado").isJsonNull()) {
                        estado = switch (s.get("idEstado").getAsInt()) {
                            case 3 -> "Pendiente";
                            case 4 -> "Programada";
                            case 5 -> "En proceso";
                            case 6 -> "Finalizado";
                            case 7 -> "Cancelado";
                            default -> "N/A";
                        };
                    }

                    String costo = "0.00";
                    if (s.has("costo") && !s.get("costo").isJsonNull()) {
                        costo = String.format(Locale.US, "$%.2f", s.get("costo").getAsDouble());
                    }

                    String fi = strSafe(s, "fechaInicio");
                    if (fi.contains("T")) fi = fi.substring(0, 10);

                    rows.add(FXCollections.observableArrayList(
                            String.valueOf(s.get("idServicio").getAsInt()),
                            strInt(s, "idCita"),
                            strSafe(s, "descripcion"),
                            costo,
                            estado,
                            strInt(s, "idTipoServicio"),
                            fi
                    ));
                }

                Platform.runLater(() -> {
                    tablaServicios.setItems(rows);
                    lblInfo.setText("Mis servicios: " + rows.size());
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblInfo.setText("Error al cargar.");
                    new Alert(Alert.AlertType.ERROR,
                            "Error:\n" + ex.getMessage(), ButtonType.OK).showAndWait();
                });
            }
        }).start();
    }

    private void llenarFormulario(ObservableList<String> fila) {
        idServicioSeleccionado = Integer.parseInt(fila.get(0));
        lblServicioId.setText("Servicio ID: " + idServicioSeleccionado);
        txtDescripcion.setText(fila.get(2).equals("—") ? "" : fila.get(2));
        // Quitar $ para editar, el valor interno siempre usa punto decimal
        txtCosto.setText(fila.get(3).replace("$", "").trim());
        cmbEstado.setValue(fila.get(4));
    }

    private void limpiarFormulario() {
        idServicioSeleccionado = -1;
        lblServicioId.setText("Servicio ID: —");
        txtDescripcion.clear();
        txtCosto.clear();
        cmbEstado.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarCambios() {
        if (idServicioSeleccionado == -1) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un servicio.", ButtonType.OK).showAndWait();
            return;
        }

        String desc   = txtDescripcion.getText().trim();
        String costoS = txtCosto.getText().trim().replace(",", ".");
        String estado = cmbEstado.getValue();

        if (costoS.isEmpty() || estado == null) {
            new Alert(Alert.AlertType.WARNING, "Costo y Estado son obligatorios.", ButtonType.OK).showAndWait();
            return;
        }

        double costo;
        try {
            costo = Double.parseDouble(costoS);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "El costo debe ser un numero valido.", ButtonType.OK).showAndWait();
            return;
        }

        int idEstado = 5;
        for (int i = 0; i < ESTADOS.length; i++) {
            if (ESTADOS[i].equals(estado)) { idEstado = ID_ESTADOS[i]; break; }
        }

        final int    idEstadoFinal = idEstado;
        final int    idServFinal   = idServicioSeleccionado;
        final String descEsc       = desc.replace("\"", "\\\"").replace("\n", "\\n");

        new Thread(() -> {
            try {
                // Locale.US garantiza punto decimal en el JSON
                String json = String.format(Locale.US,
                        "{\"idServicio\":%d,\"idTecnico\":%d,\"descripcion\":\"%s\"," +
                                "\"costo\":%.2f,\"idEstado\":%d}",
                        idServFinal, ApiConfig.ID_TECNICO, descEsc, costo, idEstadoFinal
                );
                ApiService.put("/servicio/" + idServFinal, json);
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Servicio actualizado.", ButtonType.OK).showAndWait();
                    cargarServicios();
                });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR,
                                "Error al guardar:\n" + ex.getMessage(), ButtonType.OK).showAndWait()
                );
            }
        }).start();
    }

    @FXML
    private void limpiar() {
        limpiarFormulario();
        tablaServicios.getSelectionModel().clearSelection();
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