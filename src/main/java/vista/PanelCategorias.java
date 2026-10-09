
package vista;

import controlador.CategoriaController;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelCategorias extends JPanel {

    private final CategoriaController categoriaController;

    private JTable tablaCategorias;
    private DefaultTableModel modeloTabla;

    public PanelCategorias() {

        categoriaController = new CategoriaController();

        configurarPanel();
        crearComponentes();
        cargarCategorias();
    }

    // ==========================================
    // CONFIGURACIÓN
    // ==========================================

    private void configurarPanel() {

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    // ==========================================
    // INTERFAZ
    // ==========================================

    private void crearComponentes() {

        JLabel lblTitulo = new JLabel(
                "Gestión de Categorías"
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre"},
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

        tablaCategorias = new JTable(modeloTabla);

        tablaCategorias.setRowHeight(26);
        tablaCategorias.setAutoCreateRowSorter(true);
        tablaCategorias.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        add(
                new JScrollPane(tablaCategorias),
                BorderLayout.CENTER
        );

        JButton btnNueva = new JButton("Nueva categoría");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar");

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNueva);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        btnNueva.addActionListener(
                e -> nuevaCategoria()
        );

        btnEditar.addActionListener(
                e -> editarCategoria()
        );

        btnEliminar.addActionListener(
                e -> eliminarCategoria()
        );

        btnActualizar.addActionListener(
                e -> cargarCategorias()
        );
    }

    // ==========================================
    // LISTAR CATEGORÍAS
    // ==========================================

    private void cargarCategorias() {

        modeloTabla.setRowCount(0);

        List<Categoria> categorias =
                categoriaController.listarCategorias();

        for (Categoria categoria : categorias) {

            modeloTabla.addRow(
                    new Object[]{
                            categoria.getId(),
                            categoria.getNombre()
                    }
            );
        }
    }

    // ==========================================
    // OBTENER FILA SELECCIONADA
    // ==========================================

    private int obtenerFilaModelo() {

        int filaVista = tablaCategorias.getSelectedRow();

        if (filaVista < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una categoría de la tabla.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );

            return -1;
        }

        return tablaCategorias.convertRowIndexToModel(
                filaVista
        );
    }

    // ==========================================
    // CREAR CATEGORÍA
    // ==========================================

    private void nuevaCategoria() {

        Window ventana = SwingUtilities.getWindowAncestor(this);

        DialogoCategoria dialogo = new DialogoCategoria(
                ventana,
                "Nueva categoría",
                ""
        );

        dialogo.setVisible(true);

        if (!dialogo.fueConfirmado()) {
            return;
        }

        boolean resultado = categoriaController.guardar(
                dialogo.obtenerNombre()
        );

        mostrarResultado(
                resultado,
                "Categoría registrada correctamente.",
                "No fue posible registrar la categoría. "
                        + "Compruebe que el nombre no esté repetido."
        );

        if (resultado) {
            cargarCategorias();
        }
    }

    // ==========================================
    // EDITAR CATEGORÍA
    // ==========================================

    private void editarCategoria() {

        int fila = obtenerFilaModelo();

        if (fila < 0) {
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);

        String nombreActual =
                (String) modeloTabla.getValueAt(fila, 1);

        Window ventana = SwingUtilities.getWindowAncestor(this);

        DialogoCategoria dialogo = new DialogoCategoria(
                ventana,
                "Editar categoría",
                nombreActual
        );

        dialogo.setVisible(true);

        if (!dialogo.fueConfirmado()) {
            return;
        }

        boolean resultado = categoriaController.actualizar(
                id,
                dialogo.obtenerNombre()
        );

        mostrarResultado(
                resultado,
                "Categoría actualizada correctamente.",
                "No fue posible actualizar la categoría. "
                        + "Compruebe que el nombre no esté repetido."
        );

        if (resultado) {
            cargarCategorias();
        }
    }

    // ==========================================
    // ELIMINAR CATEGORÍA
    // ==========================================

    private void eliminarCategoria() {

        int fila = obtenerFilaModelo();

        if (fila < 0) {
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);

        String nombre =
                (String) modeloTabla.getValueAt(fila, 1);

        if (categoriaController.tieneLibrosAsociados(id)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se puede eliminar \"" + nombre
                            + "\" porque tiene libros asociados.",
                    "Eliminación no permitida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la categoría \"" + nombre + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        boolean resultado = categoriaController.eliminar(id);

        mostrarResultado(
                resultado,
                "Categoría eliminada correctamente.",
                "No fue posible eliminar la categoría."
        );

        if (resultado) {
            cargarCategorias();
        }
    }

    // ==========================================
    // MOSTRAR RESULTADO
    // ==========================================

    private void mostrarResultado(
            boolean resultado,
            String mensajeExito,
            String mensajeError
    ) {

        JOptionPane.showMessageDialog(
                this,
                resultado ? mensajeExito : mensajeError,
                resultado ? "Operación exitosa" : "Error",
                resultado
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE
        );
    }
}

