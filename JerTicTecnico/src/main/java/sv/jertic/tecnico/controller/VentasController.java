package sv.jertic.tecnico.controller;

import sv.jertic.tecnico.service.ApiService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Locale;

public class VentasController {

    @FXML private TableView<ObservableList<String>>           tablaProductos;
    @FXML private TableColumn<ObservableList<String>, String> colIdProd;
    @FXML private TableColumn<ObservableList<String>, String> colNombreProd;
    @FXML private TableColumn<ObservableList<String>, String> colPrecioProd;
    @FXML private TableColumn<ObservableList<String>, String> colStockProd;

    @FXML private TableView<ObservableList<String>>           tablaCarrito;
    @FXML private TableColumn<ObservableList<String>, String> colIdCart;
    @FXML private TableColumn<ObservableList<String>, String> colNombreCart;
    @FXML private TableColumn<ObservableList<String>, String> colCantCart;
    @FXML private TableColumn<ObservableList<String>, String> colSubtotalCart;

    @FXML private ComboBox<String> cmbCliente;
    @FXML private TextField        txtCantidad;
    @FXML private Label            lblTotal;
    @FXML private Label            lblInfo;

    @FXML private TableView<ObservableList<String>>           tablaVentas;
    @FXML private TableColumn<ObservableList<String>, String> colIdVenta;
    @FXML private TableColumn<ObservableList<String>, String> colClienteVenta;
    @FXML private TableColumn<ObservableList<String>, String> colFechaVenta;
    @FXML private TableColumn<ObservableList<String>, String> colTotalVenta;

    private final java.util.List<Integer> idsCliente     = new java.util.ArrayList<>();
    private final java.util.List<int[]>   carritoIds     = new java.util.ArrayList<>();
    private final java.util.List<double[]>carritoPrecios = new java.util.ArrayList<>();
    private double totalCarrito = 0.0;

    @FXML
    public void initialize() {
        colIdProd.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue().get(0)));
        colNombreProd.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colPrecioProd.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colStockProd.setCellValueFactory( d -> new SimpleStringProperty(d.getValue().get(3)));

        colIdCart.setCellValueFactory(      d -> new SimpleStringProperty(d.getValue().get(0)));
        colNombreCart.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue().get(1)));
        colCantCart.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue().get(2)));
        colSubtotalCart.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));

        colIdVenta.setCellValueFactory(     d -> new SimpleStringProperty(d.getValue().get(0)));
        colClienteVenta.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colFechaVenta.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue().get(2)));
        colTotalVenta.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue().get(3)));

        cargarProductos();
        cargarClientes();
        cargarHistorialVentas();
    }

    private void cargarProductos() {
        new Thread(() -> {
            try {
                String json = ApiService.get("/producto");
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
                for (JsonElement el : arr) {
                    JsonObject p = el.getAsJsonObject();
                    if (!p.has("stock") || p.get("stock").isJsonNull()) continue;
                    if (p.get("stock").getAsInt() <= 0) continue;
                    rows.add(FXCollections.observableArrayList(
                            String.valueOf(p.get("idProducto").getAsInt()),
                            p.has("nombre") && !p.get("nombre").isJsonNull()
                                    ? p.get("nombre").getAsString() : "—",
                            String.format(Locale.US, "$%.2f", p.get("precio").getAsDouble()),
                            String.valueOf(p.get("stock").getAsInt())
                    ));
                }
                Platform.runLater(() -> tablaProductos.setItems(rows));
            } catch (Exception ex) {
                Platform.runLater(() -> lblInfo.setText("Error al cargar productos."));
            }
        }).start();
    }

    private void cargarClientes() {
        new Thread(() -> {
            try {
                String json = ApiService.get("/usuariocliente");
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

                java.util.List<String>  nombres = new java.util.ArrayList<>();
                java.util.List<Integer> ids     = new java.util.ArrayList<>();

                for (JsonElement el : arr) {
                    JsonObject c = el.getAsJsonObject();
                    if (!c.has("idCliente") || c.get("idCliente").isJsonNull()) continue;

                    int    idCliente = c.get("idCliente").getAsInt();
                    String nombre    = "Cliente #" + idCliente;

                    if (c.has("nombre") && !c.get("nombre").isJsonNull()) {
                        nombre = c.get("nombre").getAsString();
                        if (c.has("apellido") && !c.get("apellido").isJsonNull())
                            nombre += " " + c.get("apellido").getAsString();
                    } else if (c.has("usuario") && !c.get("usuario").isJsonNull()) {
                        JsonObject u = c.get("usuario").getAsJsonObject();
                        if (u.has("nombre") && !u.get("nombre").isJsonNull()) {
                            nombre = u.get("nombre").getAsString();
                            if (u.has("apellido") && !u.get("apellido").isJsonNull())
                                nombre += " " + u.get("apellido").getAsString();
                        }
                    }

                    ids.add(idCliente);
                    nombres.add(nombre);
                }

                Platform.runLater(() -> {
                    cmbCliente.getItems().clear();
                    idsCliente.clear();
                    idsCliente.addAll(ids);
                    cmbCliente.getItems().addAll(nombres);
                    if (ids.isEmpty()) lblInfo.setText("Sin clientes en el sistema.");
                });

            } catch (Exception ex) {
                Platform.runLater(() ->
                        lblInfo.setText("Error al cargar clientes: " + ex.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void agregarAlCarrito() {
        ObservableList<String> sel = tablaProductos.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta(Alert.AlertType.WARNING, "Selecciona un producto."); return; }

        String cantStr = txtCantidad.getText().trim();
        if (cantStr.isEmpty()) { alerta(Alert.AlertType.WARNING, "Ingresa la cantidad."); return; }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            alerta(Alert.AlertType.WARNING, "La cantidad debe ser un entero positivo.");
            return;
        }

        int stock = Integer.parseInt(sel.get(3));
        if (cantidad > stock) {
            alerta(Alert.AlertType.WARNING, "Stock insuficiente. Disponible: " + stock);
            return;
        }

        int    idProd   = Integer.parseInt(sel.get(0));
        String nombre   = sel.get(1);
        // Precio viene como "$30.00" — quitar $ y parsear con Locale.US
        double precio   = Double.parseDouble(sel.get(2).replace("$", "").trim());
        double subtotal = precio * cantidad;

        carritoIds.add(new int[]{idProd, cantidad});
        carritoPrecios.add(new double[]{precio, subtotal});
        totalCarrito += subtotal;

        tablaCarrito.getItems().add(FXCollections.observableArrayList(
                String.valueOf(idProd),
                nombre,
                String.valueOf(cantidad),
                String.format(Locale.US, "$%.2f", subtotal)
        ));
        lblTotal.setText(String.format(Locale.US, "Total: $%.2f", totalCarrito));
        txtCantidad.clear();
    }

    @FXML
    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectionModel().getSelectedIndex();
        if (fila < 0) { alerta(Alert.AlertType.WARNING, "Selecciona un item del carrito."); return; }
        totalCarrito -= carritoPrecios.get(fila)[1];
        carritoIds.remove(fila);
        carritoPrecios.remove(fila);
        tablaCarrito.getItems().remove(fila);
        lblTotal.setText(String.format(Locale.US, "Total: $%.2f", totalCarrito));
    }

    @FXML
    private void crearVenta() {
        if (cmbCliente.getSelectionModel().getSelectedIndex() < 0) {
            alerta(Alert.AlertType.WARNING, "Selecciona un cliente."); return;
        }
        if (carritoIds.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "El carrito esta vacio."); return;
        }

        int idCliente = idsCliente.get(cmbCliente.getSelectionModel().getSelectedIndex());
        final double total = totalCarrito;

        new Thread(() -> {
            try {
                // Locale.US garantiza punto decimal en el JSON, nunca coma
                String jsonVenta = String.format(Locale.US,
                        "{\"idCliente\":%d,\"total\":%.2f}",
                        idCliente, total
                );
                String respVenta = ApiService.post("/Venta", jsonVenta);
                JsonObject ventaCreada = JsonParser.parseString(respVenta).getAsJsonObject();
                int idVenta = ventaCreada.get("idVenta").getAsInt();

                for (int i = 0; i < carritoIds.size(); i++) {
                    int    idProd   = carritoIds.get(i)[0];
                    int    cantidad = carritoIds.get(i)[1];
                    double precio   = carritoPrecios.get(i)[0];
                    double subtotal = carritoPrecios.get(i)[1];

                    String jsonDetalle = String.format(Locale.US,
                            "{\"idVenta\":%d,\"idProducto\":%d,\"cantidad\":%d," +
                                    "\"precioUnitario\":%.2f,\"subtotal\":%.2f}",
                            idVenta, idProd, cantidad, precio, subtotal
                    );
                    ApiService.post("/DetalleVenta", jsonDetalle);
                }

                Platform.runLater(() -> {
                    alerta(Alert.AlertType.INFORMATION, "Venta creada. ID: " + idVenta);
                    limpiarCarrito();
                    cargarHistorialVentas();
                    cargarProductos();
                });

            } catch (Exception ex) {
                Platform.runLater(() ->
                        alerta(Alert.AlertType.ERROR, "Error al crear venta:\n" + ex.getMessage())
                );
            }
        }).start();
    }

    private void limpiarCarrito() {
        tablaCarrito.getItems().clear();
        carritoIds.clear();
        carritoPrecios.clear();
        totalCarrito = 0.0;
        lblTotal.setText("Total: $0.00");
        cmbCliente.getSelectionModel().clearSelection();
    }

    @FXML
    public void cargarHistorialVentas() {
        new Thread(() -> {
            try {
                String json = ApiService.get("/Venta");
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
                for (JsonElement el : arr) {
                    JsonObject v = el.getAsJsonObject();
                    String fecha = v.has("fecha") && !v.get("fecha").isJsonNull()
                            ? v.get("fecha").getAsString() : "—";
                    if (fecha.contains("T")) fecha = fecha.substring(0, 10);
                    rows.add(FXCollections.observableArrayList(
                            String.valueOf(v.get("idVenta").getAsInt()),
                            String.valueOf(v.get("idCliente").getAsInt()),
                            fecha,
                            String.format(Locale.US, "$%.2f", v.get("total").getAsDouble())
                    ));
                }
                Platform.runLater(() -> tablaVentas.setItems(rows));
            } catch (Exception ignored) {}
        }).start();
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        new Alert(tipo, msg, ButtonType.OK).showAndWait();
    }
}