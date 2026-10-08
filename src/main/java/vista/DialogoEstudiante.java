
package vista;

import modelo.Estudiante;

import javax.swing.*;
import java.awt.*;

public class DialogoEstudiante extends JDialog {

    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCurso;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;

    private boolean confirmado = false;
    private int idEstudiante = 0;

    public DialogoEstudiante(Window propietario) {

        super(
                propietario,
                "Registrar Estudiante",
                ModalityType.APPLICATION_MODAL
        );

        configurarVentana();
        crearComponentes();
    }

    public DialogoEstudiante(
            Window propietario,
            Estudiante estudiante
    ) {

        super(
                propietario,
                "Editar Estudiante",
                ModalityType.APPLICATION_MODAL
        );

        idEstudiante = estudiante.getId();

        configurarVentana();
        crearComponentes();
        cargarDatos(estudiante);

        // La contraseña sólo se solicita al registrar.
        txtContrasena.setEnabled(false);
        txtContrasena.setText("");
    }

    private void configurarVentana() {

        setSize(480, 440);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void crearComponentes() {

        JPanel panel = new JPanel(
                new GridBagLayout()
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(20);
        txtRut = new JTextField(20);
        txtCurso = new JTextField(20);
        txtCorreo = new JTextField(20);
        txtContrasena = new JPasswordField(20);

        agregarCampo(
                panel, gbc, 0,
                "Nombre:", txtNombre
        );

        agregarCampo(
                panel, gbc, 1,
                "RUT:", txtRut
        );

        agregarCampo(
                panel, gbc, 2,
                "Curso:", txtCurso
        );

        agregarCampo(
                panel, gbc, 3,
                "Correo:", txtCorreo
        );

        agregarCampo(
                panel, gbc, 4,
                "Contraseña:", txtContrasena
        );

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton btnCancelar =
                new JButton("Cancelar");

        JButton btnGuardar =
                new JButton("Guardar");

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        panel.add(panelBotones, gbc);

        add(panel);

        btnCancelar.addActionListener(
                e -> dispose()
        );

        btnGuardar.addActionListener(
                e -> validarFormulario()
        );

        getRootPane().setDefaultButton(btnGuardar);
    }

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String etiqueta,
            Component componente
    ) {

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;

        panel.add(
                new JLabel(etiqueta),
                gbc
        );

        gbc.gridx = 1;

        panel.add(componente, gbc);
    }

    private void cargarDatos(Estudiante estudiante) {

        txtNombre.setText(estudiante.getNombre());
        txtRut.setText(estudiante.getRut());
        txtCurso.setText(estudiante.getCurso());
        txtCorreo.setText(estudiante.getCorreo());
    }

    private void validarFormulario() {

        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String curso = txtCurso.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty()
                || rut.isEmpty()
                || curso.isEmpty()
                || correo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar todos los campos.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!correo.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
        )) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un correo electrónico válido.",
                    "Correo inválido",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (idEstudiante == 0
                && new String(txtContrasena.getPassword())
                .trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar una contraseña.",
                    "Contraseña obligatoria",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }


    public Estudiante obtenerEstudiante() {

        if (!confirmado) {
            return null;
        }

        return new Estudiante(
                idEstudiante,
                txtNombre.getText().trim(),
                txtRut.getText().trim(),
                txtCurso.getText().trim(),
                txtCorreo.getText().trim()
        );
    }


    public String obtenerContrasena() {

        return new String(
                txtContrasena.getPassword()
        );
    }
}
