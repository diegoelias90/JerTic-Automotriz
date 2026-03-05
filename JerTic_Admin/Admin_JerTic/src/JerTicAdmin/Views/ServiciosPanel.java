package JerTicAdmin.Views;

import JerTicAdmin.Services.ApiService;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel de Servicios — El admin gestiona los TIPOS de servicio que ofrece el taller
 * (Preventivo, Correctivo, Servicio Mayor, etc.) y puede ver los servicios activos.
 */
public class ServiciosPanel extends JPanel {

    // ── Tabla de tipos de servicio ─────────────────────────────────────────────
    private JTable tablaServicios;
    private DefaultTableModel modeloServicios;

    // ── Tabla de servicios activos (solo lectura) ──────────────────────────────
    private JTable tablaActivos;
    private DefaultTableModel modeloActivos;

    // ── Formulario de tipo de servicio ─────────────────────────────────────────
    private JTextField txtNombre;
    private JTextArea  txtDescripcion;
    private int idSeleccionado = -1;

    public ServiciosPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 250));

        add(construirTitulo(),   BorderLayout.NORTH);
        add(construirCentro(),   BorderLayout.CENTER);
        add(construirBotones(),  BorderLayout.SOUTH);

        cargarTiposServicio();
        cargarServiciosActivos();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TÍTULO
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 40));
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("  Servicios del Taller");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(255, 165, 0));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENTRO — dos secciones con JSplitPane vertical
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirCentro() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Lado izquierdo: tabla de tipos + formulario
        panel.add(construirSeccionTipos());

        // Lado derecho: servicios activos (solo lectura)
        panel.add(construirSeccionActivos());

        return panel;
    }

    // ── Sección izquierda: CRUD de tipos de servicio ───────────────────────────
    private JPanel construirSeccionTipos() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(245, 245, 250));

        // Sub-título
        JLabel sub = new JLabel("  Tipos de Servicio Ofrecidos");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sub.setForeground(new Color(50, 50, 65));
        sub.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 0));
        panel.add(sub, BorderLayout.NORTH);

        // Tabla de tipos
        String[] cols = {"ID", "Nombre", "Descripción"};
        modeloServicios = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaServicios = new JTable(modeloServicios);
        tablaServicios.setRowHeight(28);
        tablaServicios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaServicios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaServicios.getTableHeader().setBackground(new Color(50, 50, 65));
        tablaServicios.getTableHeader().setForeground(Color.WHITE);
        tablaServicios.setSelectionBackground(new Color(255, 165, 0, 150));
        tablaServicios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                llenarFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaServicios);
        scroll.setPreferredSize(new Dimension(0, 220));

        // Formulario de edición
        JPanel form = construirFormulario();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, form);
        split.setDividerLocation(220);
        split.setDividerSize(4);
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 6, 6, 6));

        JLabel tit = new JLabel("Datos del Tipo de Servicio");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(new Color(50, 50, 65));
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tit);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Nombre
        JLabel lblNombre = new JLabel("Nombre *");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNombre.setForeground(new Color(80, 80, 100));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtNombre);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Descripción
        JLabel lblDesc = new JLabel("Descripción");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(80, 80, 100));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDesc);
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollDesc);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botones del formulario
        JPanel botForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botForm.setBackground(new Color(245, 245, 250));
        botForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnNuevo   = botonPeq("Nuevo",   new Color(100, 149, 237));
        JButton btnGuardar = botonPeq("Guardar",    new Color(60,  179, 113));
        JButton btnElim    = botonPeq("Eliminar",   new Color(205, 92,  92));

        botForm.add(btnNuevo);
        botForm.add(btnGuardar);
        botForm.add(btnElim);
        panel.add(botForm);

        btnNuevo.addActionListener(e   -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarTipoServicio());
        btnElim.addActionListener(e    -> eliminarTipoServicio());

        return panel;
    }

    // ── Sección derecha: servicios activos (solo lectura) ──────────────────────
    private JPanel construirSeccionActivos() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(245, 245, 250));

        JLabel sub = new JLabel("  Servicios en Curso / Historial");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sub.setForeground(new Color(50, 50, 65));
        sub.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 0));
        panel.add(sub, BorderLayout.NORTH);

        String[] cols = {"ID", "Cita", "Técnico ID", "Descripción", "Costo", "Estado", "Tipo"};
        modeloActivos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaActivos = new JTable(modeloActivos) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    String estado = getModel().getValueAt(row, 5).toString();
                    switch (estado) {
                        case "En proceso"  -> c.setBackground(new Color(255, 245, 180));
                        case "Finalizado"  -> c.setBackground(new Color(200, 255, 200));
                        case "Cancelado"   -> c.setBackground(new Color(255, 200, 200));
                        default            -> c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        tablaActivos.setRowHeight(28);
        tablaActivos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaActivos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaActivos.getTableHeader().setBackground(new Color(50, 50, 65));
        tablaActivos.getTableHeader().setForeground(Color.WHITE);
        tablaActivos.setSelectionBackground(new Color(255, 165, 0, 120));

        JScrollPane scroll = new JScrollPane(tablaActivos);

        JButton btnRefreshActivos = botonPeq("Actualizar", new Color(119, 136, 153));
        btnRefreshActivos.addActionListener(e -> cargarServiciosActivos());
        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        botPanel.setBackground(new Color(245, 245, 250));
        botPanel.add(btnRefreshActivos);

        panel.add(scroll,    BorderLayout.CENTER);
        panel.add(botPanel,  BorderLayout.SOUTH);

        return panel;
    }

    //Botónes
    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(235, 235, 242));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 215)));

        JButton btnRefreshTodo = boton("Actualizar todo", new Color(119, 136, 153));
        panel.add(btnRefreshTodo);
        btnRefreshTodo.addActionListener(e -> {
            cargarTiposServicio();
            cargarServiciosActivos();
        });
        return panel;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 36));
        return btn;
    }

    private JButton botonPeq(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 30));
        return btn;
    }

    //Cargar tipo de servicios
    private void cargarTiposServicio() {
        try {
            String json = ApiService.get("/tiposervicio");
            JsonParser parser = new JsonParser(); //Aquí obtiene la respuesta como array 
            JsonArray arr = parser.parse(json).getAsJsonArray();
            modeloServicios.setRowCount(0);

            for (JsonElement el : arr) {
                JsonObject ts = el.getAsJsonObject();
                modeloServicios.addRow(new Object[]{
                    ts.get("idTipoServicio").getAsInt(),
                    ts.get("nombre").getAsString(),
                    ts.get("descripcion").getAsString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar tipos de servicio:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — CARGAR SERVICIOS ACTIVOS
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarServiciosActivos() {
        try {
            String json = ApiService.get("/servicio");
            JsonParser parser = new JsonParser(); //Aquí obtiene la respuesta como array 
            JsonArray arr = parser.parse(json).getAsJsonArray();
            modeloActivos.setRowCount(0);

            for (JsonElement el : arr) {
                JsonObject s = el.getAsJsonObject();
                int estId = s.get("idEstado").getAsInt();
                String estado = switch (estId) {
                    case 4 -> "Programado";
                    case 5 -> "En proceso";
                    case 6 -> "Finalizado";
                    case 7 -> "Cancelado";
                    default -> "N/A";
                };

                modeloActivos.addRow(new Object[]{
                    s.get("idServicio").getAsInt(),
                    s.get("idCita").getAsInt(),
                    s.get("idTecnico").getAsInt(),
                    s.get("descripcion").getAsString(),
                    String.format("$%.2f", s.get("costo").getAsDouble()),
                    estado,
                    s.get("idTipoServicio").getAsInt()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar servicios:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Parte de lógica
    //Llenar el formulario
    private void llenarFormulario() {
        int fila = tablaServicios.getSelectedRow();
        if (fila < 0) return;

        idSeleccionado = (int) modeloServicios.getValueAt(fila, 0);
        txtNombre.setText(      modeloServicios.getValueAt(fila, 1).toString());
        txtDescripcion.setText( modeloServicios.getValueAt(fila, 2).toString());
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtDescripcion.setText("");
        tablaServicios.clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — GUARDAR TIPO DE SERVICIO
    // ══════════════════════════════════════════════════════════════════════════
    private void guardarTipoServicio() {
        String nombre = txtNombre.getText().trim();
        String desc   = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre del servicio es obligatorio.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String json = idSeleccionado == -1
            ? String.format("{\"nombre\":\"%s\",\"descripcion\":\"%s\"}", nombre, desc)
            : String.format("{\"idTipoServicio\":%d,\"nombre\":\"%s\",\"descripcion\":\"%s\"}",
                idSeleccionado, nombre, desc);

        try {
            if (idSeleccionado == -1) {
                ApiService.post("/tiposervicio", json);
                JOptionPane.showMessageDialog(this, "Tipo de servicio creado.");
            } else {
                ApiService.put("/tiposervicio/" + idSeleccionado, json);
                JOptionPane.showMessageDialog(this, "Tipo de servicio actualizado.");
            }
            limpiarFormulario();
            cargarTiposServicio();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — ELIMINAR TIPO DE SERVICIO
    // ══════════════════════════════════════════════════════════════════════════
    private void eliminarTipoServicio() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccioná un tipo de servicio.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el tipo de servicio \"" + txtNombre.getText() + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ApiService.delete("/tiposervicio/" + idSeleccionado);
                JOptionPane.showMessageDialog(this, "Tipo de servicio eliminado.");
                limpiarFormulario();
                cargarTiposServicio();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


