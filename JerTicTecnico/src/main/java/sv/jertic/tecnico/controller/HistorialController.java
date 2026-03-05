package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.config.ApiConfig;
import sv.jertic.tecnico.service.ApiService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HistorialController {

    @FXML private TableView<ObservableList<String>>           tablaServicios;
    @FXML private TableColumn<ObservableList<String>, String> colIdServ;
    @FXML private TableColumn<ObservableList<String>, String> colCitaServ;
    @FXML private TableColumn<ObservableList<String>, String> colDescServ;
    @FXML private TableColumn<ObservableList<String>, String> colEstadoServ;

    @FXML private TableView<ObservableList<String>>           tablaHistorial;
    @FXML private TableColumn<ObservableList<String>, String> colIdHist;
    @FXML private TableColumn<ObservableList<String>, String> colDescHist;
    @FXML private TableColumn<ObservableList<String>, String> colFechaHist;

    @FXML private Label    lblServSeleccionado;
    @FXML private TextArea txtNuevaEntrada;
    @FXML private Label    lblInfo;

    private int idServicioSeleccionado = -1;
    private final ObservableList<ObservableList<String>> todosHistorial =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colIdServ.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue().get(0)));
        colCitaServ.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue().get(1)));
        colDescServ.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue().get(2)));
        colEstadoServ.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));

        colIdHist.setCellValueFactory(   d -> new SimpleStringProperty(d.getValue().get(0)));
        colDescHist.setCellValueFactory( d -> new SimpleStringProperty(d.getValue().get(1)));
        colFechaHist.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));

        tablaServicios.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> {
                    if (nuevo == null) return;
                    idServicioSeleccionado = Integer.parseInt(nuevo.get(0));
                    lblServSeleccionado.setText(
                            "Historial del Servicio ID: " + idServicioSeleccionado +
                                    "  (Cita: " + nuevo.get(1) + ")");
                    filtrarHistorial(idServicioSeleccionado);
                }
        );

        cargarDatos();
    }

    @FXML
    public void cargarServicios() {
        cargarDatos();
    }

    private void cargarDatos() {
        lblInfo.setText("Cargando...");
        tablaServicios.getItems().clear();
        tablaHistorial.getItems().clear();
        todosHistorial.clear();

        new Thread(() -> {
            try {
                // Cargar servicios del tecnico
                String jsonServ = ApiService.get("/servicio");
                JsonArray servArr = JsonParser.parseString(jsonServ).getAsJsonArray();
                ObservableList<ObservableList<String>> rowsServ = FXCollections.observableArrayList();

                for (JsonElement el : servArr) {
                    JsonObject s = el.getAsJsonObject();
                    if (!s.has("idTecnico") || s.get("idTecnico").isJsonNull()) continue;
                    if (s.get("idTecnico").getAsInt() != ApiConfig.ID_TECNICO) continue;

                    String estado = "Pendiente";
                    if (s.has("idEstado") && !s.get("idEstado").isJsonNull()) {
                        estado = switch (s.get("idEstado").getAsInt()) {
                            case 5 -> "En proceso";
                            case 6 -> "Finalizado";
                            case 7 -> "Cancelado";
                            default -> "Pendiente";
                        };
                    }

                    rowsServ.add(FXCollections.observableArrayList(
                            String.valueOf(s.get("idServicio").getAsInt()),
                            strInt(s, "idCita"),
                            strSafe(s, "descripcion"),
                            estado
                    ));
                }

                // Cargar todo el historial y guardar en memoria para filtrar localmente
                // No existe /historialservicios/servicio/{id} en la API
                String jsonHist = ApiService.get("/historialservicios");
                JsonArray histArr = JsonParser.parseString(jsonHist).getAsJsonArray();

                for (JsonElement el : histArr) {
                    JsonObject h = el.getAsJsonObject();
                    if (!h.has("idHistorial") || h.get("idHistorial").isJsonNull()) continue;

                    String fecha = strSafe(h, "fechaEvento");
                    if (fecha.contains("T")) fecha = fecha.substring(0, 16).replace("T", " ");

                    // Guardamos idServicio en pos 1 para poder filtrar
                    todosHistorial.add(FXCollections.observableArrayList(
                            String.valueOf(h.get("idHistorial").getAsInt()),
                            strInt(h, "idServicio"),
                            strSafe(h, "descripcion"),
                            fecha
                    ));
                }

                Platform.runLater(() -> {
                    tablaServicios.setItems(rowsServ);
                    lblInfo.setText("Selecciona un servicio para ver su historial.");
                });

            } catch (Exception ex) {
                Platform.runLater(() ->
                        lblInfo.setText("Error al cargar: " + ex.getMessage())
                );
            }
        }).start();
    }

    private void filtrarHistorial(int idServicio) {
        ObservableList<ObservableList<String>> filtrado = FXCollections.observableArrayList();
        for (ObservableList<String> row : todosHistorial) {
            if (row.get(1).equals(String.valueOf(idServicio))) {
                // Mostrar: id, descripcion, fecha
                filtrado.add(FXCollections.observableArrayList(
                        row.get(0), row.get(2), row.get(3)
                ));
            }
        }
        tablaHistorial.setItems(filtrado);
        lblInfo.setText(filtrado.isEmpty()
                ? "Sin entradas para este servicio."
                : "Entradas: " + filtrado.size());
    }

    @FXML
    private void agregarEntrada() {
        if (idServicioSeleccionado == -1) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecciona un servicio primero.", ButtonType.OK).showAndWait();
            return;
        }

        String descripcion = txtNuevaEntrada.getText().trim();
        if (descripcion.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Escribe una descripcion.", ButtonType.OK).showAndWait();
            return;
        }

        final int    idServ = idServicioSeleccionado;
        final String descEsc = descripcion.replace("\"", "\\\"").replace("\n", "\\n");

        new Thread(() -> {
            try {
                String json = String.format(
                        "{\"idServicio\":%d,\"descripcion\":\"%s\"}",
                        idServ, descEsc
                );
                ApiService.post("/historialservicios", json);
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Entrada agregada.", ButtonType.OK).showAndWait();
                    txtNuevaEntrada.clear();
                    cargarDatos();
                });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR,
                                "Error:\n" + ex.getMessage(), ButtonType.OK).showAndWait()
                );
            }
        }).start();
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