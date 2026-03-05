package JerTicAdmin.Views;

import JerTicAdmin.Services.ApiService;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsuarioPanel extends JPanel {

    //Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    //Campos del formulario
    private JTextField txtNombre, txtApellido, txtCorreo, txtTelefono, txtToken;
    private JComboBox<String> cmbTipoUsuario, cmbEstado;
    private JLabel lblIdOculto;

    //-1 = modo nuevo registro
    //Aquí porque en la bd no deja negativos y cero, por lo que si esto se manda se crashea porque 
    //no se aceptan estos tipos de datos
    private int idSeleccionado = -1;

    //Panel default para la view de usuario
    public UsuarioPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 250));

        add(construirTitulo(),  BorderLayout.NORTH);
        add(construirCentro(),  BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);

        cargarTabla();
    }

    //Título del panel
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 40));
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lbl = new JLabel("  Gestión de Usuarios");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(255, 165, 0));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    //Aquí estoy diciendo las dimensiones del panel, tipo, las divisiones que va a tener y qué se va a mostrar dónde
    //Por ejemplo, el construir tabla y formulario van a estar en la división horizontas
    private JSplitPane construirCentro() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            construirTabla(),
            construirFormulario()
        );
        split.setDividerLocation(620);
        split.setDividerSize(4);
        split.setBorder(null);
        return split;
    }

    //Aquí estoy construyendo la tabla, dónde estoy poniendo las columnas que tendrá la tabla y 
    //también la selección de alguna celda editable
    private JScrollPane construirTabla() {
        String[] cols = {"ID", "Nombre", "Apellido", "Correo", "Teléfono", "Rol", "Estado"};
        //Está haciendo un modelo de la tabla dónde dice en qué fila va a empezar y qué va a mostrar (los datos de cols)
        modelo = new DefaultTableModel(cols, 0) {
            //Y aquí está negando el evento de decir que la tabla es editable 
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(50, 50, 65));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(255, 165, 0, 120));
        tabla.setGridColor(new Color(220, 220, 230));

        //Si das clic en una fila entonces te lleva a llenar el formulario
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                llenarFormulario(); //Función de abajo dónde llena los datos del formulario de al lado 
            }
        });

        //Es para que pueda escrollear para la tabla 
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
        return scroll;
    }

    //Agreagr el formulario que está a la izquiera de la tabla
    private JPanel construirFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));

        JLabel tit = new JLabel("Datos del Usuario");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(new Color(50, 50, 65));
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tit);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        //ID oculto
        lblIdOculto = new JLabel("");
        lblIdOculto.setVisible(false);
        panel.add(lblIdOculto);

        //Inicializar campos
        txtNombre       = new JTextField();
        txtApellido     = new JTextField();
        txtCorreo       = new JTextField();
        txtTelefono     = new JTextField();
        txtToken        = new JTextField();
        cmbTipoUsuario  = new JComboBox<>(new String[]{"Administrador", "Tecnico", "Cliente"});
        cmbEstado       = new JComboBox<>(new String[]{"Activo", "Inactivo"});

        agregarCampo(panel, "Nombre *",            txtNombre);
        agregarCampo(panel, "Apellido *",           txtApellido);
        agregarCampo(panel, "Correo *",             txtCorreo);
        agregarCampo(panel, "Teléfono",             txtTelefono);
        agregarCampo(panel, "Contraseña (token) *", txtToken);
        agregarCombo(panel,  "Tipo de usuario",     cmbTipoUsuario);
        agregarCombo(panel,  "Estado",              cmbEstado);

        return panel;
    }

    //Los campos que va a estar mostrando en el formulario
    private void agregarCampo(JPanel panel, String label, JTextField campo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);

        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(campo);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    //El combobox que va a estar en el formulario (sin cantidad de datos definida)
    private void agregarCombo(JPanel panel, String label, JComboBox<?> combo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);

        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(combo);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    //Construir los botones
    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(235, 235, 242));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 215)));

        //Botones a usar 
        JButton btnNuevo    = boton("Nuevo",      new Color(100, 149, 237));
        JButton btnGuardar  = boton("Guardar",       new Color(60,  179, 113));
        JButton btnDesactivar = boton("Desactivar",      new Color(205, 92,  92));
        JButton btnRefresh  = boton("Actualizar",    new Color(119, 136, 153));

        //Aquí se añaden los botónes al panel 
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnDesactivar);
        panel.add(btnRefresh);

        btnNuevo.addActionListener(e    -> limpiarFormulario());
        btnGuardar.addActionListener(e  -> guardar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnRefresh.addActionListener(e  -> cargarTabla());

        return panel;
    }

    //Clase de botón para que todos tengan el mismo estilo
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

    //Carga la tabla con los datos de los usuarios (get)
    private void cargarTabla() {
        try {
            //Está accediendo al endpoint para get de todos los usuarios
            String json = ApiService.get("/usuario")
                ;JsonParser parser = new JsonParser();
                //Aquí obtiene la respuesta como array
                JsonArray arr = parser.parse(json).getAsJsonArray();
            modelo.setRowCount(0);

            for (JsonElement el : arr) {
                //Aquí cada fila la estoy tomando como un 
                JsonObject u = el.getAsJsonObject();

                int tipoId = u.get("idTipoUsuario").getAsInt();
                String rol = tipoId == 1 ? "Administrador" : tipoId == 2 ? "Técnico" : "Cliente";

                int estadoId = u.get("idEstado").getAsInt();
                String estado = estadoId == 1 ? "Activo" : "Inactivo";

                //Añade los datos que se han leído 
                modelo.addRow(new Object[]{
                    u.get("idUsuario").getAsInt(),
                    u.get("nombre").getAsString(),
                    u.get("apellido").getAsString(),
                    u.get("correo").getAsString(),
                    u.get("telefono").getAsString(),
                    rol,
                    estado
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar usuarios:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Aquí hace que se llene el formulario según lo que se ha seleccionado en la tabla 
    private void llenarFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        //Selecciona el id de la tabla 
        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        lblIdOculto.setText(String.valueOf(idSeleccionado));

        txtNombre.setText(  modelo.getValueAt(fila, 1).toString());
        txtApellido.setText(modelo.getValueAt(fila, 2).toString());
        txtCorreo.setText(  modelo.getValueAt(fila, 3).toString());
        txtTelefono.setText(modelo.getValueAt(fila, 4).toString());
        txtToken.setText(""); //no se muestra la contraseña por seguridad
        String rol = modelo.getValueAt(fila, 5).toString();
        cmbTipoUsuario.setSelectedItem(
            rol.equals("Técnico") ? "Tecnico" : rol
        );
        cmbEstado.setSelectedItem(modelo.getValueAt(fila, 6).toString());
    }

    //Limpia los txt
    private void limpiarFormulario() {
        idSeleccionado = -1;
        lblIdOculto.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtToken.setText("");
        cmbTipoUsuario.setSelectedIndex(2);
        cmbEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }

    //Aquí esta función se usa tanto en el post y el put, porque ambos tienen funciones similares
    private void guardar() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String token    = txtToken.getText().trim();

        //Validación básica
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nombre, Apellido y Correo son obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (idSeleccionado == -1 && token.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La contraseña es obligatoria para usuarios nuevos.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipoUsuario = cmbTipoUsuario.getSelectedIndex() + 1;//Es porque el cmbx empieza de cero, entonces como se muestra en el orden de la bd se le debe de sumar uno porque en la db empieza de 1
        int estado = cmbEstado.getSelectedIndex() == 0 ? 1 : 2;

        String json;
        String endpoint;
        boolean esNuevo = (idSeleccionado == -1);

        if (esNuevo) {
            endpoint = "/usuario";
            json = String.format(
                "{\"nombre\":\"%s\",\"apellido\":\"%s\",\"correo\":\"%s\"," +
                "\"telefono\":\"%s\",\"token\":\"%s\"," +
                "\"idTipoUsuario\":%d,\"idEstado\":%d}",
                nombre, apellido, correo, telefono, token, tipoUsuario, estado);
        } else {
            endpoint = "/usuario/" + idSeleccionado;
            json = String.format(
                "{\"idUsuario\":%d,\"nombre\":\"%s\",\"apellido\":\"%s\"," +
                "\"correo\":\"%s\",\"telefono\":\"%s\",\"token\":\"%s\"," +
                "\"idTipoUsuario\":%d,\"idEstado\":%d}",
                idSeleccionado, nombre, apellido, correo, telefono,
                token.isEmpty() ? "" : token, tipoUsuario, estado);
        }

        try {
            if (esNuevo) {
                ApiService.post(endpoint, json);
                JOptionPane.showMessageDialog(this, "Usuario creado correctamente.");
            } else {
                ApiService.put(endpoint, json);
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            }
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //El eliminar en realidad es el desactivar una cuenta
    private void desactivar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this,
                "Primero seleccioná un usuario de la tabla.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desactivar a " + txtNombre.getText() + "?",
            "Confirmar desactivación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ApiService.delete("/usuario/" + idSeleccionado);
                JOptionPane.showMessageDialog(this, "Usuario desactivado.");
                limpiarFormulario();
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al desactivar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}