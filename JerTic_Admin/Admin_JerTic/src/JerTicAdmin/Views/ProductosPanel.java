package JerTicAdmin.Views;

import JerTicAdmin.Services.ApiService;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel de Productos — CRUD completo.
 * Validación de stock no negativo se maneja tanto en la API (.NET + trigger SQL)
 * como aquí en el cliente para feedback inmediato al usuario.
 */
public class ProductosPanel extends JPanel {

    // ── Tabla ──────────────────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modelo;

    // ── Formulario ─────────────────────────────────────────────────────────────
    private JTextField txtNombre, txtDescripcion, txtPrecio, txtStock;
    private JComboBox<String> cmbTipoProducto;

    // ── Estado interno ─────────────────────────────────────────────────────────
    private int idSeleccionado = -1;

    // Tipos según tu BD: id 1-4
    private static final String[] TIPOS = {"Aceites", "Filtros", "Repuestos", "Herramientas"};

    public ProductosPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 250));

        add(construirTitulo(),  BorderLayout.NORTH);
        add(construirCentro(),  BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);

        cargarTabla();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TÍTULO
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 40));
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lbl = new JLabel("  Gestión de Productos");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(255, 165, 0));
        p.add(lbl, BorderLayout.WEST);

        // Indicador de stock bajo (aviso visual)
        JLabel aviso = new JLabel("Stock bajo = menos de 5 unidades");
        aviso.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        aviso.setForeground(new Color(255, 200, 0));
        p.add(aviso, BorderLayout.EAST);

        return p;
    }

    //Centro (formulario)
    private JSplitPane construirCentro() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            construirTabla(),
            construirFormulario()
        );
        split.setDividerLocation(640);
        split.setDividerSize(4);
        split.setBorder(null);
        return split;
    }

    // ── Tabla ──────────────────────────────────────────────────────────────────
    private JScrollPane construirTabla() {
        String[] cols = {"ID", "Nombre", "Descripción", "Precio ($)", "Stock", "Tipo"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo) {
            // Colorear filas con stock bajo en amarillo, stock 0 en rojo
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                Object stockVal = getModel().getValueAt(row, 4);
                int stock = 0;
                try { stock = Integer.parseInt(stockVal.toString()); } catch (Exception ignored) {}

                if (!isRowSelected(row)) {
                    if (stock == 0) {
                        c.setBackground(new Color(255, 180, 180)); // rojo claro
                    } else if (stock < 5) {
                        c.setBackground(new Color(255, 240, 180)); // amarillo claro
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };

        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(50, 50, 65));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(255, 165, 0, 150));
        tabla.setGridColor(new Color(220, 220, 230));

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                llenarFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
        return scroll;
    }

    // ── Formulario ─────────────────────────────────────────────────────────────
    private JPanel construirFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 10, 15));

        JLabel tit = new JLabel("Datos del Producto");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(new Color(50, 50, 65));
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tit);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        txtNombre       = new JTextField();
        txtDescripcion  = new JTextField();
        txtPrecio       = new JTextField();
        txtStock        = new JTextField();
        cmbTipoProducto = new JComboBox<>(TIPOS);

        agregarCampo(panel, "Nombre *",     txtNombre);
        agregarCampo(panel, "Descripción",  txtDescripcion);
        agregarCampo(panel, "Precio ($) *", txtPrecio);
        agregarCampo(panel, "Stock *",      txtStock);
        agregarCombo(panel, "Tipo",         cmbTipoProducto);

        // Leyenda de colores
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        JLabel leyenda = new JLabel("Información adicional:");
        leyenda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        leyenda.setForeground(new Color(80, 80, 100));
        leyenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(leyenda);

        JLabel l1 = new JLabel("■  Rojo = Stock agotado (0)");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l1.setForeground(new Color(200, 60, 60));
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l1);

        JLabel l2 = new JLabel("■  Amarillo = Stock bajo (< 5)");
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l2.setForeground(new Color(180, 140, 0));
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l2);

        return panel;
    }

    private void agregarCampo(JPanel p, String label, JTextField campo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(campo);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void agregarCombo(JPanel p, String label, JComboBox<?> combo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(combo);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BOTONES
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(235, 235, 242));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 215)));

        JButton btnNuevo    = boton("Nuevo",    new Color(100, 149, 237));
        JButton btnGuardar  = boton("Guardar",     new Color(60,  179, 113));
        JButton btnEliminar = boton("Eliminar",    new Color(205, 92,  92));
        JButton btnRefresh  = boton("Actualizar",  new Color(119, 136, 153));

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEliminar);
        panel.add(btnRefresh);

        btnNuevo.addActionListener(e    -> limpiar());
        btnGuardar.addActionListener(e  -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRefresh.addActionListener(e  -> cargarTabla());

        return panel;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — CARGAR TABLA
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarTabla() {
        try {
            String json = ApiService.get("/producto");
            JsonParser parser = new JsonParser(); //Aquí obtiene la respuesta como array 
            JsonArray arr = parser.parse(json).getAsJsonArray();
            modelo.setRowCount(0);

            for (JsonElement el : arr) {
                JsonObject p = el.getAsJsonObject();
                int tipoId = p.get("idTipoProducto").getAsInt();
                String tipo = (tipoId >= 1 && tipoId <= TIPOS.length) ? TIPOS[tipoId - 1] : "N/A";

                modelo.addRow(new Object[]{
                    p.get("idProducto").getAsInt(),
                    p.get("nombre").getAsString(),
                    p.get("descripcion").getAsString(),
                    String.format("%.2f", p.get("precio").getAsDouble()),
                    p.get("stock").getAsInt(),
                    tipo
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — LLENAR FORMULARIO
    // ══════════════════════════════════════════════════════════════════════════
    private void llenarFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        txtNombre.setText(     modelo.getValueAt(fila, 1).toString());
        txtDescripcion.setText(modelo.getValueAt(fila, 2).toString());
        txtPrecio.setText(     modelo.getValueAt(fila, 3).toString());
        txtStock.setText(      modelo.getValueAt(fila, 4).toString());
        cmbTipoProducto.setSelectedItem(modelo.getValueAt(fila, 5).toString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — LIMPIAR (modo nuevo)
    // ══════════════════════════════════════════════════════════════════════════
    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cmbTipoProducto.setSelectedIndex(0);
        tabla.clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — GUARDAR (POST o PUT)
    // ══════════════════════════════════════════════════════════════════════════
    private void guardar() {
        String nombre  = txtNombre.getText().trim();
        String desc    = txtDescripcion.getText().trim();
        String precioS = txtPrecio.getText().trim();
        String stockS  = txtStock.getText().trim();

        // Validación campos obligatorios
        if (nombre.isEmpty() || precioS.isEmpty() || stockS.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nombre, Precio y Stock son obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio;
        int stock;
        try {
            precio = Double.parseDouble(precioS);
            stock  = Integer.parseInt(stockS);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Precio debe ser un número decimal y Stock un número entero.",
                "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación stock no negativo (primera línea de defensa en el cliente)
        if (stock < 0) {
            JOptionPane.showMessageDialog(this,
                "El stock no puede ser negativo.",
                "Stock inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (precio <= 0) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser mayor a 0.",
                "Precio inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipoProducto = cmbTipoProducto.getSelectedIndex() + 1; // índice 0-3 → ID 1-4

        String json = idSeleccionado == -1
            ? String.format(
                "{\"nombre\":\"%s\",\"descripcion\":\"%s\",\"precio\":%.2f," +
                "\"stock\":%d,\"idTipoProducto\":%d}",
                nombre, desc, precio, stock, tipoProducto)
            : String.format(
                "{\"idProducto\":%d,\"nombre\":\"%s\",\"descripcion\":\"%s\"," +
                "\"precio\":%.2f,\"stock\":%d,\"idTipoProducto\":%d}",
                idSeleccionado, nombre, desc, precio, stock, tipoProducto);

        try {
            if (idSeleccionado == -1) {
                ApiService.post("/producto", json);
                JOptionPane.showMessageDialog(this, "Producto creado correctamente.");
            } else {
                ApiService.put("/producto/" + idSeleccionado, json);
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
            }
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — ELIMINAR
    // ══════════════════════════════════════════════════════════════════════════
    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccioná un producto de la tabla.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar \"" + txtNombre.getText() + "\"?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ApiService.delete("/producto/" + idSeleccionado);
                JOptionPane.showMessageDialog(this, "Producto eliminado.");
                limpiar();
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}