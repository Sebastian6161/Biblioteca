
package vista;

import controlador.EstudianteController;
import controlador.LibroController;
import controlador.PrestamoController;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelPrestamos extends JPanel {

    private final Usuario usuario;
    private final LibroController libroController;
    private final EstudianteController estudianteController;
    private final PrestamoController prestamoController;

    private JComboBox<Libro> comboLibros;
    private JComboBox<Estudiante> comboEstudiantes;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JLabel lblDisponibilidad;

    public PanelPrestamos(Usuario usuario) {
        this.usuario = usuario;
        this.libroController = new LibroController();
        this.estudianteController = new EstudianteController();
        this.prestamoController = new PrestamoController();

        configurarPanel();
        crearComponentes();
        cargarLibros();
        cargarEstudiantes();
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {
        setLayout(new BorderLayout(15, 15));

        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    // ==========================================
    // COMPONENTES DE LA INTERFAZ
    // ==========================================

    private void crearComponentes() {

        JLabel lblTitulo =
                new JLabel("Gestión de Préstamos");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        add(lblTitulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(
                new GridBagLayout()
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboEstudiantes = new JComboBox<>();
        comboLibros = new JComboBox<>();

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(
                        usuario.getRol()
                );

        // Selección de estudiante solo para bibliotecarios
        if (esBibliotecario) {

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;

            formulario.add(
                    new JLabel("Estudiante:"),
                    gbc
            );

            gbc.gridx = 1;
            gbc.weightx = 1;

            formulario.add(comboEstudiantes, gbc);
        }

        // Selección de libro
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Libro:"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(comboLibros, gbc);

        // Disponibilidad
        lblDisponibilidad =
                new JLabel("Seleccione un libro.");

        gbc.gridx = 1;
        gbc.gridy = 2;

        formulario.add(lblDisponibilidad, gbc);

        // Botones
        btnRegistrar =
                new JButton("Registrar préstamo");

        btnActualizar =
                new JButton("Actualizar libros");

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnActualizar);
        panelBotones.add(btnRegistrar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;

        formulario.add(panelBotones, gbc);

        add(formulario, BorderLayout.CENTER);

        // Eventos
        comboLibros.addActionListener(
                e -> actualizarDisponibilidad()
        );

        btnActualizar.addActionListener(
                e -> cargarLibros()
        );

        btnRegistrar.addActionListener(
                e -> registrarPrestamo()
        );
    }

    // ==========================================
    // CARGAR LIBROS
    // ==========================================

    private void cargarLibros() {

        comboLibros.removeAllItems();

        List<Libro> libros =
                libroController.listarLibros();

        for (Libro libro : libros) {
            comboLibros.addItem(libro);
        }

        actualizarDisponibilidad();
    }

    // ==========================================
    // CARGAR ESTUDIANTES
    // ==========================================

    private void cargarEstudiantes() {

        comboEstudiantes.removeAllItems();

        List<Estudiante> estudiantes =
                estudianteController.listarEstudiantes();

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(
                        usuario.getRol()
                );

        for (Estudiante estudiante : estudiantes) {

            if (esBibliotecario
                    || estudiante.getRut().equals(
                    usuario.getRut()
            )) {

                comboEstudiantes.addItem(estudiante);
            }
        }
    }

    // ==========================================
    // ACTUALIZAR DISPONIBILIDAD
    // ==========================================

    private void actualizarDisponibilidad() {

        Libro libro =
                (Libro) comboLibros.getSelectedItem();

        if (libro == null) {

            lblDisponibilidad.setText(
                    "No hay libros disponibles."
            );

            btnRegistrar.setEnabled(false);
            return;
        }

        if (libro.tieneStockDisponible()) {

            lblDisponibilidad.setText(
                    "Ejemplares disponibles: "
                            + libro.getStock()
            );

            btnRegistrar.setEnabled(true);

        } else {

            lblDisponibilidad.setText(
                    "Libro sin existencias."
            );

            btnRegistrar.setEnabled(false);
        }
    }

    // ==========================================
    // REGISTRAR PRÉSTAMO
    // ==========================================

    private void registrarPrestamo() {

        Libro libro =
                (Libro) comboLibros.getSelectedItem();

        if (libro == null
                || !libro.tieneStockDisponible()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un libro con stock disponible.",
                    "Préstamo no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(
                        usuario.getRol()
                );

        Estudiante estudiante;

        if (esBibliotecario) {

            estudiante =
                    (Estudiante) comboEstudiantes.getSelectedItem();

        } else {

            estudiante = null;

            // Buscar al estudiante por el RUT de la sesión
            for (int i = 0;
                 i < comboEstudiantes.getItemCount();
                 i++) {

                Estudiante actual =
                        comboEstudiantes.getItemAt(i);

                if (actual.getRut().equals(
                        usuario.getRut()
                )) {
                    estudiante = actual;
                    break;
                }
            }
        }

        if (estudiante == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró un estudiante válido para el préstamo.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int idEstudiante = estudiante.getId();
        int idLibro = libro.getId();

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Registrar el préstamo de \""
                                + libro.getTitulo()
                                + "\" para "
                                + estudiante.getNombre()
                                + "?",
                        "Confirmar préstamo",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Evitar operaciones duplicadas
        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(false);
        comboLibros.setEnabled(false);
        comboEstudiantes.setEnabled(false);

        // Ejecutar operación mediante SwingWorker
        prestamoController.registrarPrestamoAsync(
                idEstudiante,
                idLibro,
                usuario,
                resultado -> {

                    btnActualizar.setEnabled(true);
                    comboLibros.setEnabled(true);
                    comboEstudiantes.setEnabled(true);

                    if (resultado) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Préstamo registrado correctamente.",
                                "Operación exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "No fue posible registrar el préstamo. "
                                        + "Compruebe el stock y los datos.",
                                "Préstamo no realizado",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                    cargarLibros();
                }
        );
    }
}
