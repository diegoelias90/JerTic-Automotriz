package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.service.ApiService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Panel de Inventario — Vista de solo lectura de los productos disponibles.
 * El técnico puede consultar stock y precios antes de hacer una venta.
 */
public class ProductosController {

    @FXML private TableView<ObservableList<String>>           tablaProductos;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colNombre;
    @FXML private TableColumn<ObservableList<String>, String> colDescripcion;
    @FXML private TableColumn<ObservableList<String>, String> colPrecio;
    @FXML private TableColumn<ObservableList<String>, String> colStock;
    @FXML private TableColumn<ObservableList<String>, String> colTipo;

    @FXML private TextField txtBuscar;
    @FXML private Label     lblInfo;

    private static final String[] TIPOS = {"Aceites", "Filtros", "Repuestos", "Herramientas"};

    private ObservableList<ObservableList<String>> todosLosProductos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarProductos();

        // Filtro de búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> filtrar(nuevo));
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(         d -> new javafx.beans.property.SimpleStringProperty(d.getValue().get(0)));
        colNombre.setCellValueFactory(     d -> new javafx.beans.property.SimpleStringProperty(d.getValue().get(1)));
        colDescripcion.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().get(2)));
        colPrecio.setCellValueFactory(     d -> new javafx.beans.property.SimpleStringProperty(d.getValue().get(3)));
        colStock.setCellValueFactory(      d -> new javafx.beans.property.SimpleStringProperty(d.getValue().get(4)));
        colTipo.setCellValueFactory(       d -> new javafx.beans.property.SimpleStringProperty(d.getValue().get(5)));

        // Color según nivel de stock
        tablaProductos.setRowFactory(tv -> new TableRow<ObservableList<String>>() {
            @Override
            protected void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                try {
                    int stock = Integer.parseInt(item.get(4));
                    if (stock == 0)       setStyle("-fx-background-color: #FFB4B4;");
                    else if (stock < 5)   setStyle("-fx-background-color: #FFF0B4;");
                    else                  setStyle("");
                } catch (NumberFormatException e) { setStyle(""); }
            }
        });
    }

    @FXML
    public void cargarProductos() {
        lblInfo.setText("Cargando inventario...");
        tablaProductos.getItems().clear();
        todosLosProductos.clear();
        txtBuscar.clear();

        new Thread(() -> {
            try {
                String json = ApiService.get("/producto");
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

                for (JsonElement el : arr) {
                    JsonObject p = el.getAsJsonObject();
                    int tipoId = p.has("idTipoProducto") ? p.get("idTipoProducto").getAsInt() : 0;
                    String tipo = (tipoId >= 1 && tipoId <= TIPOS.length) ? TIPOS[tipoId - 1] : "N/A";

                    todosLosProductos.add(FXCollections.observableArrayList(
                        String.valueOf(p.get("idProducto").getAsInt()),
                        p.has("nombre")      ? p.get("nombre").getAsString()     : "—",
                        p.has("descripcion") ? p.get("descripcion").getAsString(): "—",
                        String.format("$%.2f", p.get("precio").getAsDouble()),
                        String.valueOf(p.get("stock").getAsInt()),
                        tipo
                    ));
                }

                Platform.runLater(() -> {
                    tablaProductos.setItems(FXCollections.observableArrayList(todosLosProductos));
                    lblInfo.setText("Productos en inventario: " + todosLosProductos.size() +
                        " | Rojo = sin stock | Amarillo = stock bajo (< 5)");
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblInfo.setText("Error al cargar inventario.");
                    new Alert(Alert.AlertType.ERROR, "Error:\n" + ex.getMessage(), ButtonType.OK).showAndWait();
                });
            }
        }).start();
    }

    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tablaProductos.setItems(FXCollections.observableArrayList(todosLosProductos));
            return;
        }
        String lower = texto.toLowerCase();
        ObservableList<ObservableList<String>> filtrados = FXCollections.observableArrayList();
        for (ObservableList<String> row : todosLosProductos) {
            if (row.get(1).toLowerCase().contains(lower) ||
                row.get(2).toLowerCase().contains(lower) ||
                row.get(5).toLowerCase().contains(lower)) {
                filtrados.add(row);
            }
        }
        tablaProductos.setItems(filtrados);
        lblInfo.setText("Mostrando: " + filtrados.size() + " resultado(s)");
    }
}
