package JerTicAdmin.Views;

import JerTicAdmin.Config.ApiConfig;
import JerTicAdmin.Services.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Pantalla de Login — Solo permite el acceso a usuarios con rol "Administrador".
 * Clientes y Técnicos ven un mensaje de acceso denegado.
 */
public class LoginForm extends JFrame {

    private JTextField     txtCorreo;
    private JPasswordField txtPassword;
    private JLabel         lblMensaje;
    private JButton        btnIngresar;

    public LoginForm() {
        setTitle("Jertic Automotriz — Acceso Administrador");
        setSize(440, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(construirPanel());
    }

    private JPanel construirPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(22, 22, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 20, 6, 20);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // ── Logo / título ──────────────────────────────────────────────────────
        JLabel logo = new JLabel("JERTIC", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logo.setForeground(new Color(255, 165, 0));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        g.insets = new Insets(28, 20, 2, 20);
        panel.add(logo, g);

        JLabel sub = new JLabel("Panel de Administración", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(150, 150, 170));
        g.gridy = 1;
        g.insets = new Insets(0, 20, 20, 20);
        panel.add(sub, g);

        //Correo
        g.gridwidth = 1;
        g.insets = new Insets(6, 20, 2, 20);

        JLabel lblCorreo = new JLabel("Correo electrónico");
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreo.setForeground(new Color(180, 180, 200));
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        panel.add(lblCorreo, g);

        txtCorreo = new JTextField();
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.setPreferredSize(new Dimension(0, 36));
        txtCorreo.setBackground(new Color(38, 38, 52));
        txtCorreo.setForeground(Color.WHITE);
        txtCorreo.setCaretColor(Color.WHITE);
        txtCorreo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 100), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        g.gridy = 3;
        g.insets = new Insets(2, 20, 8, 20);
        panel.add(txtCorreo, g);

        //Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPass.setForeground(new Color(180, 180, 200));
        g.gridy = 4;
        g.insets = new Insets(6, 20, 2, 20);
        panel.add(lblPass, g);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 36));
        txtPassword.setBackground(new Color(38, 38, 52));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 100), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        g.gridy = 5;
        g.insets = new Insets(2, 20, 8, 20);
        panel.add(txtPassword, g);

        //Mensaje de error
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setForeground(new Color(255, 100, 100));
        g.gridy = 6;
        g.insets = new Insets(0, 20, 4, 20);
        panel.add(lblMensaje, g);

        //Botón para ingresar
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnIngresar.setBackground(new Color(255, 165, 0));
        btnIngresar.setForeground(new Color(22, 22, 32));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setPreferredSize(new Dimension(0, 42));
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        g.gridy = 7;
        g.insets = new Insets(6, 20, 20, 20);
        panel.add(btnIngresar, g);

        //Evento (lo que hace el login)
        btnIngresar.addActionListener(e -> intentarLogin());

        //Enter en el campo de contraseña también dispara el login
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });

        return panel;
    }

    //Esta parte es la lógica de el login
    private void intentarLogin() {
        String correo = txtCorreo.getText().trim();
        String pass   = new String(txtPassword.getPassword()).trim();

        //Validación de campos vacíos
        if (correo.isEmpty() || pass.isEmpty()) {
            mostrarError("Completá todos los campos.");
            return;
        }

        //Deshabilitar botón durante la petición
        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");
        lblMensaje.setForeground(new Color(180, 180, 200));
        lblMensaje.setText("Conectando con el servidor...");

        //Ejecutar en hilo separado para no bloquear la UI
        new Thread(() -> {
            boolean ok = AuthService.login(correo, pass);

            SwingUtilities.invokeLater(() -> {
                btnIngresar.setEnabled(true);
                btnIngresar.setText("Ingresar");

                if (!ok) {
                    // Login fallido (credenciales incorrectas o usuario inactivo)
                    mostrarError("Correo o contraseña incorrectos.");
                    txtPassword.setText("");
                    return;
                }

                // ─ Verificar que el rol sea Administrador ─────────────────────
                if (!ApiConfig.ROL.equals("Administrador")) {
                    // Limpiar el token inmediatamente — no se le permite el acceso
                    ApiConfig.JWT_TOKEN = "";
                    ApiConfig.ROL       = "";
                    ApiConfig.NOMBRE    = "";

                    mostrarError("Acceso denegado. Solo administradores pueden ingresar.");
                    txtPassword.setText("");
                    return;
                }

                // ─ Login exitoso como administrador ───────────────────────────
                dispose();
                new MainFrame().setVisible(true);
            });
        }).start();
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setForeground(new Color(255, 100, 100));
        lblMensaje.setText(mensaje);
    }
}