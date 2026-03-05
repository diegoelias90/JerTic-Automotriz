package JerTicAdmin.Views;

import JerTicAdmin.Services.ApiService;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel de Citas — El admin ve todas las citas solicitadas por clientes
 * y puede asignar un técnico creando un servicio a partir de la cita.
 */
public class CitasPanel extends JPanel {

    // ── Tabla de citas ─────────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modelo;

    // ── Panel de detalle / asignación ─────────────────────────────────────────
    private JLabel lblIdCita, lblCliente, lblFecha, lblHora, lblMotivo, lblEstado;
    private JComboBox<String> cmbTecnico;   // técnicos cargados de la API
    private JComboBox<String> cmbTipoServicio;
    private JTextField txtCosto;
    private JTextArea  txtDescServicio;

    // IDs paralelos a los combos
    private java.util.List<Integer> idsTecnicos      = new java.util.ArrayList<>();
    private java.util.List<Integer> idsTipoServicio  = new java.util.ArrayList<>();

    private int idCitaSeleccionada = -1;

    public CitasPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 250));

        add(construirTitulo(),   BorderLayout.NORTH);
        add(construirCentro(),   BorderLayout.CENTER);
        add(construirBotones(),  BorderLayout.SOUTH);

        cargarTecnicos();
        cargarTiposServicio();
        cargarTabla();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TÍTULO
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 40));
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lbl = new JLabel("  Citas de Clientes");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(255, 165, 0));
        p.add(lbl, BorderLayout.WEST);

        JLabel sub = new JLabel("Seleccioná una cita para asignar un técnico  ");
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sub.setForeground(new Color(180, 180, 200));
        p.add(sub, BorderLayout.EAST);

        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CENTRO — tabla arriba / detalle + asignación abajo
    // ══════════════════════════════════════════════════════════════════════════
    private JSplitPane construirCentro() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            construirTabla(),
            construirPanelAsignacion()
        );
        split.setDividerLocation(620);
        split.setDividerSize(4);
        split.setBorder(null);
        return split;
    }

    // ── Tabla de citas ─────────────────────────────────────────────────────────
    private JScrollPane construirTabla() {
        String[] cols = {"ID Cita", "ID Cliente", "Fecha", "Hora", "Motivo", "Estado"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo) {
            // Colorear según estado de la cita
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    String estado = getModel().getValueAt(row, 5).toString();
                    switch (estado) {
                        case "Programada"  -> c.setBackground(new Color(200, 230, 255));
                        case "En proceso"  -> c.setBackground(new Color(255, 245, 180));
                        case "Finalizado"  -> c.setBackground(new Color(200, 255, 200));
                        case "Cancelado"   -> c.setBackground(new Color(255, 200, 200));
                        default            -> c.setBackground(Color.WHITE);
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
                llenarDetalle();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
        return scroll;
    }

    // ── Panel de detalle y asignación de técnico ───────────────────────────────
    private JPanel construirPanelAsignacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 15));

        // ─ Detalle de la cita seleccionada ─
        JLabel secCita = new JLabel("Detalle de la cita");
        secCita.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secCita.setForeground(new Color(50, 50, 65));
        secCita.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(secCita);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        lblIdCita  = infoLabel("ID Cita: —");
        lblCliente = infoLabel("Cliente ID: —");
        lblFecha   = infoLabel("Fecha: —");
        lblHora    = infoLabel("Hora: —");
        lblMotivo  = infoLabel("Motivo: —");
        lblEstado  = infoLabel("Estado: —");

        for (JLabel lbl : new JLabel[]{lblIdCita, lblCliente, lblFecha, lblHora, lblMotivo, lblEstado}) {
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lbl);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
        }

        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(200, 200, 215));
        panel.add(sep);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        // ─ Asignación de técnico y creación de servicio ─
        JLabel secAsig = new JLabel("Asignar Técnico y crear Servicio");
        secAsig.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secAsig.setForeground(new Color(50, 50, 65));
        secAsig.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(secAsig);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Técnico
        JLabel lblTec = etiqueta("Técnico asignado *");
        panel.add(lblTec);
        cmbTecnico = new JComboBox<>();
        cmbTecnico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cmbTecnico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTecnico.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbTecnico);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Tipo de servicio
        JLabel lblTipoS = etiqueta("Tipo de servicio *");
        panel.add(lblTipoS);
        cmbTipoServicio = new JComboBox<>();
        cmbTipoServicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cmbTipoServicio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoServicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbTipoServicio);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Costo
        JLabel lblCosto = etiqueta("Costo ($) *");
        panel.add(lblCosto);
        txtCosto = new JTextField();
        txtCosto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtCosto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCosto.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtCosto);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Descripción del servicio
        JLabel lblDescS = etiqueta("Descripción del servicio");
        panel.add(lblDescS);
        txtDescServicio = new JTextArea(3, 20);
        txtDescServicio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescServicio.setLineWrap(true);
        txtDescServicio.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescServicio);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollDesc);

        return panel;
    }

    private JLabel infoLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 60, 80));
        return lbl;
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BOTONES
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(235, 235, 242));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 215)));

        JButton btnAsignar  = boton("Asignar Técnico",  new Color(60, 179, 113));
        JButton btnCancelar = boton("Cancelar Cita",    new Color(205, 92, 92));
        JButton btnRefresh  = boton("Actualizar",       new Color(119, 136, 153));

        panel.add(btnAsignar);
        panel.add(btnCancelar);
        panel.add(btnRefresh);

        btnAsignar.addActionListener(e  -> asignarTecnico());
        btnCancelar.addActionListener(e -> cancelarCita());
        btnRefresh.addActionListener(e  -> { cargarTecnicos(); cargarTiposServicio(); cargarTabla(); });

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
        btn.setPreferredSize(new Dimension(160, 36));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — CARGAR TÉCNICOS desde la API
    // ══════════════════════════════════════════════════════════════════════════
        private void cargarTecnicos() {
            try {
                // Cargar desde usuariotecnico para obtener el id_tecnico real
                String jsonTecnicos = ApiService.get("/usuariotecnico");
                String jsonUsuarios = ApiService.get("/usuario");

                JsonArray tecnicos  = new JsonParser().parse(jsonTecnicos).getAsJsonArray();
                JsonArray usuarios  = new JsonParser().parse(jsonUsuarios).getAsJsonArray();

                // Mapear id_usuario → nombre completo
                java.util.Map<Integer, String> nombres = new java.util.HashMap<>();
                for (JsonElement el : usuarios) {
                    JsonObject u = el.getAsJsonObject();
                    int idU = u.get("idUsuario").getAsInt();
                    nombres.put(idU, u.get("nombre").getAsString() + " " + u.get("apellido").getAsString());
                }

                cmbTecnico.removeAllItems();
                idsTecnicos.clear();

                for (JsonElement el : tecnicos) {
                    JsonObject t = el.getAsJsonObject();
                    int idTecnico  = t.get("idTecnico").getAsInt();
                    int idUsuario  = t.has("idUsuario") && !t.get("idUsuario").isJsonNull()
                                     ? t.get("idUsuario").getAsInt() : -1;

                    String nombre = nombres.getOrDefault(idUsuario, "Tecnico #" + idTecnico);
                    idsTecnicos.add(idTecnico);   // ← id_tecnico, no id_usuario
                    cmbTecnico.addItem(nombre);
                }
            } catch (Exception ex) {
                // fallback silencioso
            }
        }
    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — CARGAR TIPOS DE SERVICIO
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarTiposServicio() {
        try {
            String json = ApiService.get("/tiposervicio");
            JsonParser parser = new JsonParser(); //Aquí obtiene la respuesta como array 
            JsonArray arr = parser.parse(json).getAsJsonArray();

            cmbTipoServicio.removeAllItems();
            idsTipoServicio.clear();

            for (JsonElement el : arr) {
                JsonObject ts = el.getAsJsonObject();
                idsTipoServicio.add(ts.get("idTipoServicio").getAsInt());
                cmbTipoServicio.addItem(ts.get("nombre").getAsString());
            }
        } catch (Exception ex) {
            // fallback: cargar los tipos hardcodeados de la BD si la API no responde
            String[] fallback = {"Preventivo","Correctivo","Servicio Menor","Servicio Mayor","Especializado","Vehículo Eléctrico"};
            for (int i = 0; i < fallback.length; i++) {
                idsTipoServicio.add(i + 1);
                cmbTipoServicio.addItem(fallback[i]);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — CARGAR TABLA DE CITAS
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarTabla() {
        try {
            String json = ApiService.get("/cita");
            JsonParser parser = new JsonParser(); //Aquí obtiene la respuesta como array 
            JsonArray arr = parser.parse(json).getAsJsonArray();
            modelo.setRowCount(0);

            for (JsonElement el : arr) {
                JsonObject c = el.getAsJsonObject();
                int estId = c.get("idEstado").getAsInt();
                String estado = switch (estId) {
                    case 4 -> "Programada";
                    case 5 -> "En proceso";
                    case 6 -> "Finalizado";
                    case 7 -> "Cancelado";
                    default -> "Pendiente";
                };

                modelo.addRow(new Object[]{
                    c.get("idCita").getAsInt(),
                    c.get("idCliente").getAsInt(),
                    c.get("fecha").getAsString(),
                    c.get("hora").getAsString(),
                    c.get("motivo").getAsString(),
                    estado
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar citas:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — LLENAR DETALLE al seleccionar una fila
    // ══════════════════════════════════════════════════════════════════════════
    private void llenarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        idCitaSeleccionada = (int) modelo.getValueAt(fila, 0);

        lblIdCita.setText( "ID Cita: "    + modelo.getValueAt(fila, 0));
        lblCliente.setText("Cliente ID: " + modelo.getValueAt(fila, 1));
        lblFecha.setText(  "Fecha: "      + modelo.getValueAt(fila, 2));
        lblHora.setText(   "Hora: "       + modelo.getValueAt(fila, 3));
        lblMotivo.setText( "Motivo: "     + modelo.getValueAt(fila, 4));
        lblEstado.setText( "Estado: "     + modelo.getValueAt(fila, 5));

        // Limpiar campos de asignación
        txtCosto.setText("");
        txtDescServicio.setText("");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — ASIGNAR TÉCNICO (crea un Servicio vinculado a la cita)
    // ══════════════════════════════════════════════════════════════════════════
    private void asignarTecnico() {
        if (idCitaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccioná una cita de la tabla primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cmbTecnico.getSelectedIndex() < 0 || idsTecnicos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay técnicos disponibles. Verificá la conexión.",
                "Sin técnicos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String costoS = txtCosto.getText().trim();
        String desc   = txtDescServicio.getText().trim();

        if (costoS.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El costo del servicio es obligatorio.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double costo;
        try {
            costo = Double.parseDouble(costoS);
            if (costo < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El costo debe ser un número positivo.",
                "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener IDs seleccionados de los combos
        int idTecnico     = idsTecnicos.get(cmbTecnico.getSelectedIndex());
        int idTipoServicio = idsTipoServicio.get(cmbTipoServicio.getSelectedIndex());

        // Construir JSON para POST /servicio
        // Estado 5 = "En proceso" al asignar
        String json = String.format(
            "{\"idCita\":%d,\"idTecnico\":%d,\"descripcion\":\"%s\"," +
            "\"costo\":%.2f,\"idEstado\":5,\"idTipoServicio\":%d}",
            idCitaSeleccionada, idTecnico,
            desc.isEmpty() ? "Servicio asignado por administrador" : desc,
            costo, idTipoServicio);

        try {
            ApiService.post("/servicio", json);

            // También actualizar el estado de la cita a "En proceso" (5)
            String jsonCita = String.format("{\"idEstado\":5}", idCitaSeleccionada);
            ApiService.put("/cita/" + idCitaSeleccionada + "/estado", jsonCita);

            JOptionPane.showMessageDialog(this,
                "Técnico asignado y servicio creado correctamente.\n" +
                "La cita pasó a estado 'En proceso'.");

            cargarTabla();
            limpiarDetalle();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al asignar técnico:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA — CANCELAR CITA
    // ══════════════════════════════════════════════════════════════════════════
    private void cancelarCita() {
        if (idCitaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccioná una cita de la tabla primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estadoActual = lblEstado.getText().replace("Estado: ", "");
        if (estadoActual.equals("Cancelado") || estadoActual.equals("Finalizado")) {
            JOptionPane.showMessageDialog(this,
                "Esta cita ya está " + estadoActual.toLowerCase() + " y no se puede cancelar.",
                "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Cancelar la cita ID " + idCitaSeleccionada + "?\nEsta acción no se puede deshacer.",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Estado 7 = Cancelado
                String json = "{\"idEstado\":7}";
                ApiService.put("/cita/" + idCitaSeleccionada + "/estado", json);
                JOptionPane.showMessageDialog(this, "Cita cancelada.");
                cargarTabla();
                limpiarDetalle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al cancelar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarDetalle() {
        idCitaSeleccionada = -1;
        lblIdCita.setText( "ID Cita: —");
        lblCliente.setText("Cliente ID: —");
        lblFecha.setText(  "Fecha: —");
        lblHora.setText(   "Hora: —");
        lblMotivo.setText( "Motivo: —");
        lblEstado.setText( "Estado: —");
        txtCosto.setText("");
        txtDescServicio.setText("");
        tabla.clearSelection();
    }
}