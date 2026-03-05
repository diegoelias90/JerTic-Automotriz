package JerTicAdmin.Views;

import JerTicAdmin.Config.ApiConfig;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel  contentPanel;
    private JButton btnActivo = null;

    public MainFrame() {
        setTitle("Jertic Automotriz — Panel Administrador");
        setSize(1250, 750);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(construirSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 250));
        add(contentPanel, BorderLayout.CENTER);

        mostrarPanel(new UsuarioPanel(), null);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(18, 18, 28));
        sidebar.setPreferredSize(new Dimension(200, 750));

        // ── Logo ────────────────────────────────────────────────────────────
        sidebar.add(Box.createRigidArea(new Dimension(0, 28)));

        JLabel logo = new JLabel("JERTIC", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(new Color(255, 165, 0));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        JLabel logoSub = new JLabel("Automotriz", SwingConstants.CENTER);
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSub.setForeground(new Color(100, 100, 120));
        logoSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoSub);

        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Tarjeta de usuario ───────────────────────────────────────────────
        // Panel con fondo ligeramente más claro para destacar al usuario
        JPanel cardUsuario = new JPanel();
        cardUsuario.setLayout(new BoxLayout(cardUsuario, BoxLayout.Y_AXIS));
        cardUsuario.setBackground(new Color(28, 28, 42));
        cardUsuario.setMaximumSize(new Dimension(174, 62));
        cardUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardUsuario.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel nombreAdmin = new JLabel(ApiConfig.NOMBRE);
        nombreAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nombreAdmin.setForeground(new Color(220, 220, 235));
        nombreAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardUsuario.add(nombreAdmin);

        JLabel rolLabel = new JLabel(ApiConfig.ROL);
        rolLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rolLabel.setForeground(new Color(255, 165, 0));
        rolLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardUsuario.add(rolLabel);

        sidebar.add(cardUsuario);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));

        // ── Separador ────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(160, 1));
        sep.setForeground(new Color(45, 45, 62));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // ── Etiqueta de menú ─────────────────────────────────────────────────
        JLabel lblMenu = new JLabel("MENÚ");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenu.setForeground(new Color(80, 80, 100));
        lblMenu.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMenu.setMaximumSize(new Dimension(200, 20));
        sidebar.add(lblMenu);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        // ── Botones de navegación ─────────────────────────────────────────────
        // Cada botón tiene un ícono de texto + etiqueta
        JButton btnUsuarios  = botonMenu("Usuarios");
        JButton btnProductos = botonMenu("Productos");
        JButton btnCitas     = botonMenu("Citas");
        JButton btnServicios = botonMenu("Servicios");
        JButton btnVentas    = botonMenu("Ventas");

        sidebar.add(btnUsuarios);
        sidebar.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebar.add(btnProductos);
        sidebar.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebar.add(btnCitas);
        sidebar.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebar.add(btnServicios);
        sidebar.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebar.add(btnVentas);

        // Acciones
        btnUsuarios.addActionListener(e  -> mostrarPanel(new UsuarioPanel(),  btnUsuarios));
        btnProductos.addActionListener(e -> mostrarPanel(new ProductosPanel(), btnProductos));
        btnCitas.addActionListener(e     -> mostrarPanel(new CitasPanel(),     btnCitas));
        btnServicios.addActionListener(e -> mostrarPanel(new ServiciosPanel(), btnServicios));
        btnVentas.addActionListener(e    -> mostrarPanel(new VentasPanel(),    btnVentas));

        activarBoton(btnUsuarios);

        // ── Espacio flexible ─────────────────────────────────────────────────
        sidebar.add(Box.createVerticalGlue());

        // ── Botón cerrar sesión ──────────────────────────────────────────────
        // Usa el mismo estilo que los otros botones pero con color rojo
        JPanel panelSalir = new JPanel(new BorderLayout());
        panelSalir.setBackground(new Color(18, 18, 28));
        panelSalir.setMaximumSize(new Dimension(200, 56));
        panelSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSalir.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(38, 38, 55)));

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setBackground(new Color(18, 18, 28));
        btnSalir.setForeground(new Color(220, 80, 80));
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setOpaque(true);
        btnSalir.setHorizontalAlignment(SwingConstants.LEFT);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnSalir.setBackground(new Color(60, 20, 20));
                btnSalir.setForeground(new Color(255, 100, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnSalir.setBackground(new Color(18, 18, 28));
                btnSalir.setForeground(new Color(220, 80, 80));
            }
        });

        btnSalir.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                ApiConfig.JWT_TOKEN = "";
                ApiConfig.ROL       = "";
                ApiConfig.NOMBRE    = "";
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        panelSalir.add(btnSalir, BorderLayout.CENTER);
        sidebar.add(panelSalir);

        return sidebar;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Botón del sidebar — diseño limpio, sin apariencia fea de Swing
    // ──────────────────────────────────────────────────────────────────────────
    private JButton botonMenu(String textoFijo) {
        JButton btn = new JButton(textoFijo);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setBackground(new Color(18, 18, 28));   // igual que el sidebar
        btn.setForeground(new Color(180, 180, 200));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Los colores se guardan como variables para el hover — nunca tocamos el texto
        Color bgNormal = new Color(18, 18, 28);
        Color bgHover  = new Color(30, 30, 46);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) btn.setBackground(bgHover);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) btn.setBackground(bgNormal);
            }
        });

        return btn;
    }

    // Resalta el botón activo con línea naranja a la izquierda
    private void activarBoton(JButton btn) {
        if (btnActivo != null) {
            btnActivo.setBackground(new Color(18, 18, 28));
            btnActivo.setForeground(new Color(180, 180, 200));
            btnActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnActivo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        }
        btn.setBackground(new Color(36, 32, 18));           // fondo cálido oscuro
        btn.setForeground(new Color(255, 165, 0));          // texto naranja
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));   // negrita
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(255, 165, 0)), // línea naranja izq
            BorderFactory.createEmptyBorder(0, 17, 0, 0)
        ));
        btnActivo = btn;
    }

    //Mostrar el panel
    private void mostrarPanel(JPanel panel, JButton boton) {
        if (boton != null) activarBoton(boton);
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}