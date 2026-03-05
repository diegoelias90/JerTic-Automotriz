/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JerTicAdmin;
import JerTicAdmin.Views.LoginForm;
import javax.swing.SwingUtilities;
/**
 *
 * @author djelo
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
