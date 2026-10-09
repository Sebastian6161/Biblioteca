
package vista;

import controlador.EstudianteController;
import controlador.LibroController;
import controlador.PrestamoController;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelDevoluciones extends JPanel {

    private final Usuario usuario;
    private final PrestamoController prestamoController;
    private final EstudianteController estudianteController;
    private final LibroController libroController;

    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private JButton btnDevolver;
    private JButton btnActualizar;

    private final Map<Integer, Estudiante> estudiantesPorId =
            new HashMap<>();

    private final Map<Integer, Libro> librosPorId =
            new HashMap<>();

    public PanelDevoluciones(Usuario usuario) {

        this.usuario = usuario;
        this.prestamoController = new PrestamoController();
        this.estudianteController = new EstudianteController();
        this.libroController = new LibroController();

        configurarPanel();
        crearComponentes();
        cargarPrestamos();
    }

    // ==========================================
    // CONFIGURACIÓN DEL PANEL
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );
    }

    // ==========================================
    // COMPONENTES DE LA INTERFAZ
    // ==========================================

    private void crearComponentes() {

        JLabel lblTitulo =
                new JLabel("Gestión de Devoluciones");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Estudiante",
                        "Libro",
                        "Fecha préstamo",
                        "Fecha límite",
                        "Estado"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);

        tablaPrestamos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaPrestamos.setAutoCreateRowSorter(true);
        tablaPrestamos.setRowHeight(26);

        add(
                new JScrollPane(tablaPrestamos),
                BorderLayout.CENTER
        );

        btnDevolver =
                new JButton("Registrar devolución");

        btnActualizar =
                new JButton("Actualizar");

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnActualizar);
        panelBotones.add(btnDevolver);

        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnActualizar.addActionListener(
                e -> cargarPrestamos()
        );

        btnDevolver.addActionListener(
                e -> registrarDevolucion()
        );
    }

    // ==========================================
    // CARGAR PRÉSTAMOS ACTIVOS
    // ==========================================

    private void cargarPrestamos() {

        modeloTabla.setRowCount(0);

        estudiantesPorId.clear();
        librosPorId.clear();

        List<Estudiante> estudiantes =
                estudianteController.listarEstudiantes();

        for (Estudiante estudiante : estudiantes) {

            estudiantesPorId.put(
                    estudiante.getId(),
                    estudiante
            );
        }

        List<Libro> libros =
                libroController.listarLibros();

        for (Libro libro : libros) {
            librosPorId.put(
                    libro.getId(),
                    libro
            );
        }

        List<Prestamo> prestamos =
                prestamoController.listarPrestamosActivos();

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(
                        usuario.getRol()
                );

        for (Prestamo prestamo : prestamos) {

            Estudiante estudiante =
                    estudiantesPorId.get(
                            prestamo.getIdEstudiante()
                    );

            Libro libro =
                    librosPorId.get(
                            prestamo.getIdLibro()
                    );

            if (estudiante == null || libro == null) {
                continue;
            }

            // El estudiante solo visualiza sus préstamos
            if (!esBibliotecario
                    && !estudiante.getRut().equals(
                    usuario.getRut()
            )) {
                continue;
            }

            modeloTabla.addRow(
                    new Object[]{
                            prestamo.getId(),
                            estudiante.getNombre(),
                            libro.getTitulo(),
                            prestamo.getFechaPrestamo(),
                            prestamo.getFechaDevolucion(),
                            prestamo.estaAtrasado()
                                    ? "Atrasado"
                                    : "Pendiente"
                    }
            );
        }

        btnDevolver.setEnabled(
                modeloTabla.getRowCount() > 0
        );
    }

    // ==========================================
    // REGISTRAR DEVOLUCIÓN
    // ==========================================

    private void registrarDevolucion() {

        int filaVista =
                tablaPrestamos.getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un préstamo de la tabla.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int filaModelo =
                tablaPrestamos.convertRowIndexToModel(
                        filaVista
                );

        int idPrestamo =
                (int) modeloTabla.getValueAt(
                        filaModelo,
                        0
                );

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Confirmar devolución del préstamo N.º "
                                + idPrestamo + "?",
                        "Confirmar devolución",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Evitar operaciones duplicadas
        btnDevolver.setEnabled(false);
        btnActualizar.setEnabled(false);

        // Ejecutar devolución mediante SwingWorker
        prestamoController.registrarDevolucionAsync(
                idPrestamo,
                usuario,
                resultado -> {

                    btnActualizar.setEnabled(true);

                    if (resultado) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Devolución registrada correctamente.",
                                "Operación exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "No fue posible registrar la devolución.",
                                "Operación no realizada",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                    cargarPrestamos();
                }
        );
    }
}
