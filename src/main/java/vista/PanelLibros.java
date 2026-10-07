package vista;

import controlador.LibroController;
import modelo.Libro;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelLibros extends JPanel {

    private final Usuario usuario;
    private final LibroController libroController;

    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;

    public PanelLibros(Usuario usuario) {

        this.usuario = usuario;
        this.libroController = new LibroController();

        configurarPanel();
        crearComponentes();
        cargarLibros();
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

        // =========================
        // TÍTULO
        // =========================

        JLabel lblTitulo =
                new JLabel("Gestión de Libros");

        lblTitulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        add(
                lblTitulo,
                BorderLayout.NORTH
        );

        // =========================
        // TABLA
        // =========================

        String[] columnas = {
                "ID",
                "Título",
                "Autor",
                "ISBN",
                "Editorial",
                "Stock",
                "Categoría"
        };

        modeloTabla =
                new DefaultTableModel(
                        columnas,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        tablaLibros =
                new JTable(modeloTabla);

        tablaLibros.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaLibros.setAutoCreateRowSorter(true);

        JScrollPane scrollPane =
                new JScrollPane(tablaLibros);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =========================
        // BOTONES
        // =========================

        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        btnActualizar =
                new JButton("Actualizar");

        panelBotones.add(btnActualizar);

        if ("bibliotecario".equalsIgnoreCase(
                usuario.getRol())) {

            btnNuevo =
                    new JButton("Nuevo");

            btnEditar =
                    new JButton("Editar");

            btnEliminar =
                    new JButton("Eliminar");

            panelBotones.add(btnNuevo);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);
        }

        add(
                panelBotones,
                BorderLayout.SOUTH
        );

        // =========================
        // EVENTOS
        // =========================

        btnActualizar.addActionListener(
                e -> cargarLibros()
        );

        if (btnNuevo != null) {
            btnNuevo.addActionListener(
                    e -> nuevoLibro()
            );
        }

        if (btnEditar != null) {
            btnEditar.addActionListener(
                    e -> editarLibro()
            );
        }

        if (btnEliminar != null) {
            btnEliminar.addActionListener(
                    e -> eliminarLibro()
            );
        }
    }

    private void cargarLibros() {

        modeloTabla.setRowCount(0);

        List<Libro> libros =
                libroController.listarLibros();

        for (Libro libro : libros) {

            Object[] fila = {
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getEditorial(),
                    libro.getStock(),
                    libroController.obtenerNombreCategoria(
                            libro.getIdCategoria()
                    )
            };

            modeloTabla.addRow(fila);
        }
    }
    private void nuevoLibro() {

        Window ventana =
                SwingUtilities.getWindowAncestor(this);

        DialogoLibro dialogo =
                new DialogoLibro(ventana);

        dialogo.setVisible(true);

        if (!dialogo.isConfirmado()) {
            return;
        }

        Libro libro =
                dialogo.obtenerLibro();

        boolean guardado =
                libroController.guardar(libro);

        if (guardado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Libro registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarLibros();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible registrar el libro.\n"
                            + "Verifique los datos o el ISBN.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void editarLibro() {

        int fila =
                tablaLibros.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un libro.",
                    "Editar libro",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int filaModelo =
                tablaLibros.convertRowIndexToModel(
                        fila
                );

        int idLibro =
                (Integer) modeloTabla.getValueAt(
                        filaModelo,
                        0
                );

        Libro libro =
                libroController.buscarPorId(
                        idLibro
                );

        if (libro == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible encontrar el libro.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        Window ventana =
                SwingUtilities.getWindowAncestor(
                        this
                );

        DialogoLibro dialogo =
                new DialogoLibro(
                        ventana,
                        libro
                );

        dialogo.setVisible(true);

        if (!dialogo.isConfirmado()) {
            return;
        }

        Libro libroEditado =
                dialogo.obtenerLibro();

        boolean actualizado =
                libroController.actualizar(
                        libroEditado
                );

        if (actualizado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Libro actualizado correctamente.",
                    "Actualización exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarLibros();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible actualizar el libro.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void eliminarLibro() {

        int fila =
                tablaLibros.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un libro.",
                    "Eliminar libro",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int filaModelo =
                tablaLibros.convertRowIndexToModel(
                        fila
                );

        int idLibro =
                (Integer) modeloTabla.getValueAt(
                        filaModelo,
                        0
                );

        String titulo =
                modeloTabla.getValueAt(
                        filaModelo,
                        1
                ).toString();

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de eliminar el libro?\n\n"
                                + titulo,
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmacion
                != JOptionPane.YES_OPTION) {

            return;
        }

        boolean eliminado =
                libroController.eliminar(
                        idLibro
                );

        if (eliminado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Libro eliminado correctamente.",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarLibros();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible eliminar el libro.\n"
                            + "Puede tener préstamos asociados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}