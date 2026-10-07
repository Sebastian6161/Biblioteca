package vista;

import dao.CategoriaDAO;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogoLibro extends JDialog {

    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtIsbn;
    private JTextField txtEditorial;
    private JSpinner spnStock;
    private JComboBox<Categoria> cmbCategoria;
    private int idLibro = 0;


    private boolean confirmado = false;

    public DialogoLibro(
            Window propietario,
            Libro libro
    ) {

        super(
                propietario,
                "Editar Libro",
                ModalityType.APPLICATION_MODAL
        );

        this.idLibro = libro.getId();

        configurarVentana();
        crearComponentes();
        cargarCategorias();
        cargarDatosLibro(libro);
    }

    public DialogoLibro(Window propietario) {

        super(
                propietario,
                "Nuevo Libro",
                ModalityType.APPLICATION_MODAL
        );

        configurarVentana();
        crearComponentes();
        cargarCategorias();
    }

    private void configurarVentana() {

        setSize(450, 420);
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

        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTitulo = new JTextField(20);
        txtAutor = new JTextField(20);
        txtIsbn = new JTextField(20);
        txtEditorial = new JTextField(20);

        spnStock = new JSpinner(
                new SpinnerNumberModel(
                        0,
                        0,
                        9999,
                        1
                )
        );

        cmbCategoria = new JComboBox<>();

        agregarCampo(
                panel,
                gbc,
                0,
                "Título:",
                txtTitulo
        );

        agregarCampo(
                panel,
                gbc,
                1,
                "Autor:",
                txtAutor
        );

        agregarCampo(
                panel,
                gbc,
                2,
                "ISBN:",
                txtIsbn
        );

        agregarCampo(
                panel,
                gbc,
                3,
                "Editorial:",
                txtEditorial
        );

        agregarCampo(
                panel,
                gbc,
                4,
                "Stock:",
                spnStock
        );

        agregarCampo(
                panel,
                gbc,
                5,
                "Categoría:",
                cmbCategoria
        );

        JPanel panelBotones =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT
                ));

        JButton btnCancelar =
                new JButton("Cancelar");

        JButton btnGuardar =
                new JButton("Guardar");

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;

        panel.add(panelBotones, gbc);

        add(panel);

        btnCancelar.addActionListener(
                e -> dispose()
        );

        btnGuardar.addActionListener(
                e -> validarFormulario()
        );

        getRootPane().setDefaultButton(
                btnGuardar
        );
    }

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String texto,
            Component componente
    ) {

        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.gridwidth = 1;

        panel.add(
                new JLabel(texto),
                gbc
        );

        gbc.gridx = 1;

        panel.add(
                componente,
                gbc
        );
    }

    private void cargarCategorias() {

        CategoriaDAO categoriaDAO =
                new CategoriaDAO();

        List<Categoria> categorias =
                categoriaDAO.listar();

        cmbCategoria.removeAllItems();

        for (Categoria categoria : categorias) {
            cmbCategoria.addItem(categoria);
        }
    }

    private void validarFormulario() {

        if (txtTitulo.getText().trim().isEmpty()
                || txtAutor.getText().trim().isEmpty()
                || txtIsbn.getText().trim().isEmpty()
                || txtEditorial.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar todos los campos.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (cmbCategoria.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar una categoría.",
                    "Categoría",
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

    public Libro obtenerLibro() {

        if (!confirmado) {
            return null;
        }

        Categoria categoria =
                (Categoria) cmbCategoria
                        .getSelectedItem();

        return new Libro(
                idLibro,
                txtTitulo.getText().trim(),
                txtAutor.getText().trim(),
                txtIsbn.getText().trim(),
                txtEditorial.getText().trim(),
                (Integer) spnStock.getValue(),
                categoria.getId()
        );
    }
    private void cargarDatosLibro(Libro libro) {

        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn());
        txtEditorial.setText(libro.getEditorial());
        spnStock.setValue(libro.getStock());

        for (int i = 0;
             i < cmbCategoria.getItemCount();
             i++) {

            Categoria categoria =
                    cmbCategoria.getItemAt(i);

            if (categoria.getId()
                    == libro.getIdCategoria()) {

                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }
}
