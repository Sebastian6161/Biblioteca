package vista;

import controlador.LoginController;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {

    private final JTextField txtCorreo;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;

    private final LoginController loginController;

    public VentanaLogin() {

        loginController = new LoginController();

        setTitle("Biblioteca Escolar - Inicio de Sesión");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(
                new GridBagLayout()
        );

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 35, 25, 35
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo =
                new JLabel("Biblioteca Escolar");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        lblTitulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panelPrincipal.add(lblTitulo, gbc);

        JLabel lblSubtitulo =
                new JLabel("Inicio de Sesión");

        lblSubtitulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        gbc.gridy = 1;

        panelPrincipal.add(lblSubtitulo, gbc);

        // Correo
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;

        panelPrincipal.add(
                new JLabel("Correo:"),
                gbc
        );

        txtCorreo = new JTextField(20);

        gbc.gridx = 1;

        panelPrincipal.add(
                txtCorreo,
                gbc
        );

        // Contraseña
        gbc.gridy = 3;
        gbc.gridx = 0;

        panelPrincipal.add(
                new JLabel("Contraseña:"),
                gbc
        );

        txtContrasena =
                new JPasswordField(20);

        gbc.gridx = 1;

        panelPrincipal.add(
                txtContrasena,
                gbc
        );

        // Botón
        btnIngresar =
                new JButton("Ingresar");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        panelPrincipal.add(
                btnIngresar,
                gbc
        );

        add(panelPrincipal);

        btnIngresar.addActionListener(
                e -> iniciarSesion()
        );

        getRootPane().setDefaultButton(
                btnIngresar
        );
    }

    private void iniciarSesion() {

        String correo =
                txtCorreo.getText();

        String contrasena =
                new String(
                        txtContrasena.getPassword()
                );

        if (correo.trim().isEmpty()
                || contrasena.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar correo y contraseña.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Usuario usuario =
                loginController.iniciarSesion(
                        correo,
                        contrasena
                );

        if (usuario != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Bienvenido/a "
                            + usuario.getNombre(),
                    "Inicio de sesión correcto",
                    JOptionPane.INFORMATION_MESSAGE
            );

            VentanaPrincipal ventanaPrincipal =
                    new VentanaPrincipal(usuario);

            ventanaPrincipal.setVisible(true);

            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Correo o contraseña incorrectos.",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE
            );

            txtContrasena.setText("");
        }
    }
}