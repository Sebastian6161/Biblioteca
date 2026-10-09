
package vista;

import controlador.EstudianteController;
import controlador.ReporteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelReportes extends JPanel {

    private final ReporteController reporteController;
    private final EstudianteController estudianteController;

    private JComboBox<String> comboReportes;
    private JComboBox<Estudiante> comboEstudiantes;

    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;

    private JButton btnActualizar;
    private JLabel lblTotal;

    public PanelReportes() {

        this.reporteController = new ReporteController();
        this.estudianteController = new EstudianteController();

        configurarPanel();
        crearComponentes();
        cargarEstudiantes();
        actualizarReporte();
    }

    // ==========================================
    // CONFIGURACIÓN GENERAL
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
    // CREACIÓN DE COMPONENTES
    // ==========================================

    private void crearComponentes() {

        JLabel lblTitulo = new JLabel(
                "Reportes de la Biblioteca"
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        add(lblTitulo, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(
                new BorderLayout(10, 10)
        );

        // Filtros
        JPanel panelFiltros = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5)
        );

        comboReportes = new JComboBox<>(
                new String[]{
                        "Libros más prestados",
                        "Historial por estudiante",
                        "Préstamos activos",
                        "Préstamos atrasados"
                }
        );

        comboEstudiantes = new JComboBox<>();

        btnActualizar = new JButton(
                "Actualizar reporte"
        );

        panelFiltros.add(
                new JLabel("Tipo de reporte:")
        );

        panelFiltros.add(comboReportes);

        panelFiltros.add(
                new JLabel("Estudiante:")
        );

        panelFiltros.add(comboEstudiantes);
        panelFiltros.add(btnActualizar);

        panelCentral.add(
                panelFiltros,
                BorderLayout.NORTH
        );

        // Tabla
        modeloTabla = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };

        tablaReportes = new JTable(modeloTabla);

        tablaReportes.setRowHeight(26);
        tablaReportes.setAutoCreateRowSorter(true);

        panelCentral.add(
                new JScrollPane(tablaReportes),
                BorderLayout.CENTER
        );

        add(panelCentral, BorderLayout.CENTER);

        // Pie de página
        lblTotal = new JLabel(
                "Registros: 0"
        );

        add(lblTotal, BorderLayout.SOUTH);

        // Eventos
        comboReportes.addActionListener(
                e -> actualizarReporte()
        );

        comboEstudiantes.addActionListener(
                e -> {
                    if (comboReportes.getSelectedIndex() == 1) {
                        actualizarReporte();
                    }
                }
        );

        btnActualizar.addActionListener(
                e -> {
                    cargarEstudiantes();
                    actualizarReporte();
                }
        );
    }

    // ==========================================
    // CARGAR ESTUDIANTES
    // ==========================================

    private void cargarEstudiantes() {

        Estudiante seleccionado =
                (Estudiante) comboEstudiantes.getSelectedItem();

        Integer idSeleccionado =
                seleccionado == null
                        ? null
                        : seleccionado.getId();

        comboEstudiantes.removeAllItems();

        List<Estudiante> estudiantes =
                estudianteController.listarEstudiantes();

        for (Estudiante estudiante : estudiantes) {

            comboEstudiantes.addItem(estudiante);

            if (idSeleccionado != null
                    && estudiante.getId() == idSeleccionado) {

                comboEstudiantes.setSelectedItem(estudiante);
            }
        }
    }

    // ==========================================
    // ACTUALIZAR REPORTE
    // ==========================================

    private void actualizarReporte() {

        int tipoReporte =
                comboReportes.getSelectedIndex();

        String[] columnas;
        List<Object[]> resultados;

        comboEstudiantes.setEnabled(
                tipoReporte == 1
        );

        switch (tipoReporte) {

            case 0 -> {

                columnas = new String[]{
                        "Título",
                        "Autor",
                        "Total de préstamos"
                };

                resultados =
                        reporteController.obtenerLibrosMasPrestados();
            }

            case 1 -> {

                columnas = new String[]{
                        "Libro",
                        "Fecha préstamo",
                        "Fecha límite",
                        "Estado"
                };

                Estudiante estudiante =
                        (Estudiante)
                                comboEstudiantes.getSelectedItem();

                if (estudiante == null) {
                    resultados = List.of();
                } else {
                    resultados =
                            reporteController
                                    .obtenerHistorialEstudiante(
                                            estudiante.getId()
                                    );
                }
            }

            case 2 -> {

                columnas = new String[]{
                        "Estudiante",
                        "Libro",
                        "Fecha préstamo",
                        "Fecha límite"
                };

                resultados =
                        reporteController.obtenerPrestamosActivos();
            }

            case 3 -> {

                columnas = new String[]{
                        "Estudiante",
                        "Libro",
                        "Fecha límite",
                        "Días de atraso"
                };

                resultados =
                        reporteController.obtenerPrestamosAtrasados();
            }

            default -> {
                columnas = new String[]{};
                resultados = List.of();
            }
        }

        // Actualizar columnas y registros
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(columnas);

        for (Object[] fila : resultados) {
            modeloTabla.addRow(fila);
        }

        lblTotal.setText(
                "Registros: " + resultados.size()
        );
    }
}
