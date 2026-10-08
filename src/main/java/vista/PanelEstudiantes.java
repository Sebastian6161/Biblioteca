
package vista;

import controlador.EstudianteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelEstudiantes extends JPanel {

    private final EstudianteController estudianteController;

    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;

    public PanelEstudiantes() {

        estudianteController = new EstudianteController();

        configurarPanel();
        crearComponentes();
        cargarEstudiantes();
    }

    private void configurarPanel() {

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );
    }

    private void crearComponentes() {

        JLabel lblTitulo =
                new JLabel("Gestión de Estudiantes");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        add(lblTitulo, BorderLayout.NORTH);

        String[] columnas = {
                "ID",
                "Nombre",
                "RUT",
                "Curso",
                "Correo"
        };

        modeloTabla = new DefaultTableModel(
                columnas, 0
        ) {
            @Override
            public boolean isCellEditable(
                    int row, int column
            ) {
                return false;
            }
        };

        tablaEstudiantes = new JTable(modeloTabla);

        tablaEstudiantes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaEstudiantes.setAutoCreateRowSorter(true);

        JScrollPane scrollPane =
                new JScrollPane(tablaEstudiantes);

        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        btnActualizar = new JButton("Actualizar");
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(
                e -> cargarEstudiantes()
        );

        btnNuevo.addActionListener(
                e -> registrarEstudiante()
        );

        btnEditar.addActionListener(
                e -> editarEstudiante()
        );

        btnEliminar.addActionListener(
                e -> eliminarEstudiante()
        );
    }

    private void cargarEstudiantes() {

        modeloTabla.setRowCount(0);

        List<Estudiante> estudiantes =
                estudianteController.listarEstudiantes();

        for (Estudiante estudiante : estudiantes) {

            Object[] fila = {
                    estudiante.getId(),
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCurso(),
                    estudiante.getCorreo()
            };

            modeloTabla.addRow(fila);
        }
    }

    private void registrarEstudiante() {

        Window ventanaPadre =
                SwingUtilities.getWindowAncestor(this);

        DialogoEstudiante dialogo =
                new DialogoEstudiante(ventanaPadre);

        dialogo.setVisible(true);

        // Si el usuario presiona Cancelar, no hacemos nada.
        if (!dialogo.isConfirmado()) {
            return;
        }

        Estudiante estudiante =
                dialogo.obtenerEstudiante();

        String contrasena =
                dialogo.obtenerContrasena();

        boolean registrado =
                estudianteController.registrarConCuenta(
                        estudiante,
                        contrasena
                );

        if (registrado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Estudiante y cuenta de usuario "
                            + "registrados correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarEstudiantes();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible registrar al estudiante.\n"
                            + "Comprueba que el RUT y el correo "
                            + "no estén registrados.",
                    "Error de registro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void editarEstudiante() {

        int filaSeleccionada = tablaEstudiantes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un estudiante para editar.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Convertimos la fila visual a la fila real del modelo.
        int filaModelo = tablaEstudiantes.convertRowIndexToModel(
                filaSeleccionada
        );

        int idEstudiante = (int) modeloTabla.getValueAt(
                filaModelo, 0
        );

        Estudiante estudianteOriginal =
                estudianteController.buscarPorId(idEstudiante);

        if (estudianteOriginal == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible encontrar al estudiante.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Conservamos el RUT original para encontrar su cuenta.
        String rutOriginal = estudianteOriginal.getRut();

        Window ventanaPadre =
                SwingUtilities.getWindowAncestor(this);

        DialogoEstudiante dialogo = new DialogoEstudiante(
                ventanaPadre,
                estudianteOriginal
        );

        dialogo.setVisible(true);

        if (!dialogo.isConfirmado()) {
            return;
        }

        Estudiante estudianteEditado =
                dialogo.obtenerEstudiante();

        boolean actualizado =
                estudianteController.actualizarConCuenta(
                        estudianteEditado,
                        rutOriginal
                );

        if (actualizado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Estudiante y cuenta de usuario "
                            + "actualizados correctamente.",
                    "Actualización exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarEstudiantes();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible actualizar al estudiante.\n"
                            + "Compruebe que el RUT y el correo "
                            + "no pertenezcan a otro usuario.",
                    "Error de actualización",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminarEstudiante() {

        int filaSeleccionada = tablaEstudiantes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un estudiante para eliminar.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // La tabla permite ordenar las filas.
        int filaModelo = tablaEstudiantes.convertRowIndexToModel(
                filaSeleccionada
        );

        int idEstudiante = (int) modeloTabla.getValueAt(
                filaModelo, 0
        );

        Estudiante estudiante =
                estudianteController.buscarPorId(idEstudiante);

        if (estudiante == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró al estudiante seleccionado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al estudiante?\n\n"
                        + "Nombre: " + estudiante.getNombre() + "\n"
                        + "RUT: " + estudiante.getRut() + "\n\n"
                        + "También se eliminará su cuenta de usuario.\n"
                        + "Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        boolean eliminado =
                estudianteController.eliminar(idEstudiante);

        if (eliminado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Estudiante y cuenta de usuario "
                            + "eliminados correctamente.",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarEstudiantes();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible eliminar al estudiante.\n\n"
                            + "Puede tener préstamos registrados "
                            + "o existir un problema con sus datos.\n"
                            + "Revise la consola para más información.",
                    "Eliminación no realizada",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
