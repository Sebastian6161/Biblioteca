
package vista;

import javax.swing.*;
import java.awt.*;

public class DialogoCategoria extends JDialog {

    private final JTextField txtNombre;
    private boolean confirmado = false;

    public DialogoCategoria(
            Window propietario,
            String titulo,
            String nombreInicial
    ) {
        super(propietario, titulo, ModalityType.APPLICATION_MODAL);

        setSize(420, 210);
        setLocationRelativeTo(propietario);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel formulario = new JPanel(new BorderLayout(5, 8));

        formulario.add(
                new JLabel("Nombre de la categoría:"),
                BorderLayout.NORTH
        );

        txtNombre = new JTextField(
                nombreInicial == null ? "" : nombreInicial
        );

        formulario.add(txtNombre, BorderLayout.CENTER);
        panel.add(formulario, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());

        btnCancelar.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnGuardar);

        setContentPane(panel);

        SwingUtilities.invokeLater(
                txtNombre::requestFocusInWindow
        );
    }

    private void guardar() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre de la categoría.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (nombre.length() > 100) {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre no puede superar 100 caracteres.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        confirmado = true;
        dispose();
    }

    public boolean fueConfirmado() {
        return confirmado;
    }

    public String obtenerNombre() {
        return txtNombre.getText().trim();
    }
}
