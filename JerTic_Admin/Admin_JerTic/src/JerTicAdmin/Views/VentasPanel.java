package JerTicAdmin.Views;

import JerTicAdmin.Services.ApiService;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentasPanel extends JPanel {

    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;
    private JTextArea txtComentario;
    private JLabel    lblResumen;

    private int idVentaSeleccionada = -1;

    public VentasPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 250));
        add(construirTitulo(),  BorderLayout.NORTH);
        add(construirCentro(),  BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);
        cargarVentas();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TÍTULO
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 40));
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("  Registro de Ventas");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(255, 165, 0));
        p.add(lbl, BorderLayout.WEST);
        JLabel sub = new JLabel("Solo lectura — podés agregar comentarios internos  ");
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sub.setForeground(new Color(150, 150, 170));
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENTRO
    // ══════════════════════════════════════════════════════════════════════════
    private JSplitPane construirCentro() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            construirTablaVentas(), construirPanelDerecho());
        split.setDividerLocation(430);
        split.setDividerSize(4);
        split.setBorder(null);
        return split;
    }

    // ── Tabla ventas ───────────────────────────────────────────────────────────
    private JScrollPane construirTablaVentas() {
        String[] cols = {"ID Venta", "Cliente ID", "Fecha", "Total ($)"};
        modeloVentas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaVentas = new JTable(modeloVentas);
        tablaVentas.setRowHeight(30);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaVentas.getTableHeader().setBackground(new Color(50, 50, 65));
        tablaVentas.getTableHeader().setForeground(Color.WHITE);
        tablaVentas.setSelectionBackground(new Color(255, 165, 0, 150));
        tablaVentas.setGridColor(new Color(220, 220, 230));
        tablaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { verDetalle(); }
        });
        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
        return scroll;
    }

    // ── Panel derecho ──────────────────────────────────────────────────────────
    private JPanel construirPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 12));

        // Resumen de la venta seleccionada
        lblResumen = new JLabel("  Seleccioná una venta para ver el detalle");
        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResumen.setForeground(new Color(60, 60, 80));
        lblResumen.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(lblResumen, BorderLayout.NORTH);

        // ── Tabla de detalle ───────────────────────────────────────────────────
        String[] colsDet = {"ID Prod.", "Cantidad", "Precio Unit. ($)", "Subtotal ($)"};
        modeloDetalle = new DefaultTableModel(colsDet, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setRowHeight(28);
        tablaDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDetalle.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaDetalle.getTableHeader().setBackground(new Color(70, 70, 85));
        tablaDetalle.getTableHeader().setForeground(Color.WHITE);
        tablaDetalle.setGridColor(new Color(220, 220, 230));

        JScrollPane scrollDet = new JScrollPane(tablaDetalle);
        scrollDet.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 225)));

        // ── Sección comentario ─────────────────────────────────────────────────
        JPanel panelCom = new JPanel(new BorderLayout(0, 6));
        panelCom.setBackground(new Color(245, 245, 250));
        panelCom.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel lblCom = new JLabel("Comentario / Nota interna:");
        lblCom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCom.setForeground(new Color(60, 60, 80));
        panelCom.add(lblCom, BorderLayout.NORTH);

        txtComentario = new JTextArea(4, 20);
        txtComentario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scrollCom = new JScrollPane(txtComentario);
        scrollCom.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 215)));
        panelCom.add(scrollCom, BorderLayout.CENTER);

        // Botón guardar comentario — ancho completo, sin apariencia fea
        JButton btnGuardar = new JButton("Guardar comentario");
        btnGuardar.setBackground(new Color(60, 179, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(0, 40));
        btnGuardar.addActionListener(e -> guardarComentario());
        panelCom.add(btnGuardar, BorderLayout.SOUTH);

        // Split vertical: tabla detalle arriba, comentario abajo
        JSplitPane splitV = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollDet, panelCom);
        splitV.setDividerLocation(220);
        splitV.setDividerSize(4);
        splitV.setBorder(null);
        splitV.setBackground(new Color(245, 245, 250));

        panel.add(splitV, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BOTONES BARRA INFERIOR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panel.setBackground(new Color(235, 235, 242));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 215)));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.setBackground(new Color(100, 116, 139));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setOpaque(true);
        btnRefresh.setPreferredSize(new Dimension(130, 36));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> cargarVentas());
        panel.add(btnRefresh);

        JLabel info = new JLabel("Las ventas son de solo lectura. Podés agregar comentarios internos a cada una.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(new Color(120, 120, 140));
        panel.add(info);

        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarVentas() {
        try {
            String json = ApiService.get("/Venta");
            JsonParser parser = new JsonParser();
                //Aquí obtiene la respuesta como array
            JsonArray arr = parser.parse(json).getAsJsonArray();
            modeloVentas.setRowCount(0);
            for (JsonElement el : arr) {
                JsonObject v = el.getAsJsonObject();
                String fecha = v.get("fecha").getAsString();
                if (fecha.contains("T")) fecha = fecha.substring(0, 10);
                modeloVentas.addRow(new Object[]{
                    v.get("idVenta").getAsInt(),
                    v.get("idCliente").getAsInt(),
                    fecha,
                    String.format("%.2f", v.get("total").getAsDouble())
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar ventas:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetalle() {
        int fila = tablaVentas.getSelectedRow();
        if (fila < 0) return;

        idVentaSeleccionada = (int) modeloVentas.getValueAt(fila, 0);
        lblResumen.setText(
            "  Venta #" + idVentaSeleccionada +
            "   |   Cliente ID: " + modeloVentas.getValueAt(fila, 1) +
            "   |   Fecha: "      + modeloVentas.getValueAt(fila, 2) +
            "   |   Total: $"     + modeloVentas.getValueAt(fila, 3)
        );

        txtComentario.setText("");
        modeloDetalle.setRowCount(0);

        try {
            // GET /api/DetalleVenta/venta/{id}
            String json = ApiService.get("/DetalleVenta/venta/" + idVentaSeleccionada);
            JsonParser parser = new JsonParser();
                //Aquí obtiene la respuesta como array
                JsonArray arr = parser.parse(json).getAsJsonArray();

            if (arr.size() == 0) {
                modeloDetalle.addRow(new Object[]{"—", "Sin productos registrados", "—", "—"});
                return;
            }
            for (JsonElement el : arr) {
                JsonObject d = el.getAsJsonObject();
                modeloDetalle.addRow(new Object[]{
                    d.get("idProducto").getAsInt(),
                    d.get("cantidad").getAsInt(),
                    String.format("$%.2f", d.get("precioUnitario").getAsDouble()),
                    String.format("$%.2f", d.get("subtotal").getAsDouble())
                });
            }
        } catch (Exception ex) {
            modeloDetalle.addRow(new Object[]{"—", "Error: " + ex.getMessage(), "—", "—"});
        }
    }

    private void guardarComentario() {
        if (idVentaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná una venta primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String comentario = txtComentario.getText().trim();
        if (comentario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribí un comentario antes de guardar.",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Escapar caracteres especiales del JSON
        String esc = comentario
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "");

        try {
            // POST /api/Venta/{id}/comentario
            // (PATCH no funciona en HttpURLConnection de Java)
            ApiService.post("/Venta/" + idVentaSeleccionada + "/comentario",
                String.format("{\"comentario\":\"%s\"}", esc));
            JOptionPane.showMessageDialog(this, "Comentario guardado correctamente.",
                "Guardado", JOptionPane.INFORMATION_MESSAGE);
            txtComentario.setText("");
        } catch (Exception ex) {
            String msg = ex.getMessage() != null && ex.getMessage().contains("422")
                ? "Este cliente no tiene servicios registrados aún.\nEl comentario no puede guardarse en el historial hasta que tenga al menos un servicio."
                : "Error al guardar comentario:\n" + ex.getMessage();
            JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}