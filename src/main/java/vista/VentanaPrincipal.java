
package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;
    private JPanel panelContenido;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;

        configurarVentana();
        crearInterfaz();
    }

    // ==========================================
    // CONFIGURACIÓN DE LA VENTANA
    // ==========================================

    private void configurarVentana() {

        setTitle("Biblioteca Escolar - Sistema de Gestión");
        setSize(1000, 650);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
    }

    // ==========================================
    // CREACIÓN DE LA INTERFAZ
    // ==========================================

    private void crearInterfaz() {

        setLayout(new BorderLayout());

        // ======================================
        // CABECERA
        // ======================================

        JPanel panelSuperior = new JPanel(
                new BorderLayout()
        );

        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 20, 15, 20
                )
        );

        JLabel lblTitulo = new JLabel(
                "Biblioteca Escolar"
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        JLabel lblUsuario = new JLabel(
                usuario.getNombre()
                        + " | "
                        + usuario.getRol()
        );

        panelSuperior.add(
                lblTitulo,
                BorderLayout.WEST
        );

        panelSuperior.add(
                lblUsuario,
                BorderLayout.EAST
        );

        add(
                panelSuperior,
                BorderLayout.NORTH
        );

        // ======================================
        // MENÚ LATERAL
        // ======================================

        JPanel panelMenu = new JPanel();

        panelMenu.setLayout(
                new BoxLayout(
                        panelMenu,
                        BoxLayout.Y_AXIS
                )
        );

        panelMenu.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 15, 20, 15
                )
        );

        panelMenu.setPreferredSize(
                new Dimension(210, 0)
        );

        JButton btnInicio = crearBoton("Inicio");
        JButton btnLibros = crearBoton("Libros");
        JButton btnPrestamos = crearBoton("Préstamos");
        JButton btnDevoluciones = crearBoton("Devoluciones");

        panelMenu.add(btnInicio);
        panelMenu.add(Box.createVerticalStrut(10));

        panelMenu.add(btnLibros);
        panelMenu.add(Box.createVerticalStrut(10));

        // ======================================
        // OPCIONES DEL BIBLIOTECARIO
        // ======================================

        if (esBibliotecario()) {

            JButton btnEstudiantes =
                    crearBoton("Estudiantes");

            JButton btnReportes =
                    crearBoton("Reportes");

            panelMenu.add(btnEstudiantes);
            panelMenu.add(Box.createVerticalStrut(10));

            panelMenu.add(btnReportes);
            panelMenu.add(Box.createVerticalStrut(10));

            btnEstudiantes.addActionListener(
                    e -> mostrarPanelEstudiantes()
            );

            btnReportes.addActionListener(
                    e -> mostrarPanelReportes()
            );
        }

        // ======================================
        // OPCIONES COMPARTIDAS
        // ======================================

        panelMenu.add(btnPrestamos);
        panelMenu.add(Box.createVerticalStrut(10));

        panelMenu.add(btnDevoluciones);
        panelMenu.add(Box.createVerticalGlue());

        JButton btnCerrarSesion =
                crearBoton("Cerrar sesión");

        panelMenu.add(btnCerrarSesion);

        add(
                panelMenu,
                BorderLayout.WEST
        );

        // ======================================
        // PANEL DE CONTENIDO
        // ======================================

        panelContenido = new JPanel(
                new BorderLayout()
        );

        panelContenido.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        add(
                panelContenido,
                BorderLayout.CENTER
        );

        mostrarInicio();

        // ======================================
        // EVENTOS
        // ======================================

        btnInicio.addActionListener(
                e -> mostrarInicio()
        );

        btnLibros.addActionListener(
                e -> mostrarPanelLibros()
        );

        btnPrestamos.addActionListener(
                e -> mostrarPanelPrestamos()
        );

        btnDevoluciones.addActionListener(
                e -> mostrarPanelDevoluciones()
        );

        btnCerrarSesion.addActionListener(
                e -> cerrarSesion()
        );
    }

    // ==========================================
    // VALIDACIÓN DE ROL
    // ==========================================

    private boolean esBibliotecario() {

        return "bibliotecario".equalsIgnoreCase(
                usuario.getRol()
        );
    }

    // ==========================================
    // CREACIÓN DE BOTONES
    // ==========================================

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);

        boton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        boton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return boton;
    }

    // ==========================================
    // PANTALLA DE INICIO
    // ==========================================

    private void mostrarInicio() {

        panelContenido.removeAll();

        JPanel panelInicio = new JPanel(
                new GridLayout(3, 1, 10, 10)
        );

        JLabel lblBienvenida = new JLabel(
                "Bienvenido/a, " + usuario.getNombre()
        );

        lblBienvenida.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JLabel lblRol = new JLabel(
                "Rol: " + usuario.getRol()
        );

        JLabel lblDescripcion = new JLabel(
                "Seleccione una opción del menú para comenzar."
        );

        panelInicio.add(lblBienvenida);
        panelInicio.add(lblRol);
        panelInicio.add(lblDescripcion);

        panelContenido.add(
                panelInicio,
                BorderLayout.NORTH
        );

        actualizarContenido();
    }

    // ==========================================
    // PANEL DE LIBROS
    // ==========================================

    private void mostrarPanelLibros() {

        panelContenido.removeAll();

        PanelLibros panelLibros =
                new PanelLibros(usuario);

        panelContenido.add(
                panelLibros,
                BorderLayout.CENTER
        );

        actualizarContenido();
    }

    // ==========================================
    // PANEL DE ESTUDIANTES
    // ==========================================

    private void mostrarPanelEstudiantes() {

        if (!esBibliotecario()) {

            mostrarAccesoDenegado(
                    "No tiene permisos para gestionar estudiantes."
            );

            return;
        }

        panelContenido.removeAll();

        PanelEstudiantes panelEstudiantes =
                new PanelEstudiantes();

        panelContenido.add(
                panelEstudiantes,
                BorderLayout.CENTER
        );

        actualizarContenido();
    }

    // ==========================================
    // PANEL DE REPORTES
    // ==========================================

    private void mostrarPanelReportes() {

        if (!esBibliotecario()) {

            mostrarAccesoDenegado(
                    "No tiene permisos para consultar los reportes."
            );

            return;
        }

        panelContenido.removeAll();

        PanelReportes panelReportes =
                new PanelReportes();

        panelContenido.add(
                panelReportes,
                BorderLayout.CENTER
        );

        actualizarContenido();
    }

    // ==========================================
    // PANEL DE PRÉSTAMOS
    // ==========================================

    private void mostrarPanelPrestamos() {

        panelContenido.removeAll();

        PanelPrestamos panelPrestamos =
                new PanelPrestamos(usuario);

        panelContenido.add(
                panelPrestamos,
                BorderLayout.CENTER
        );

        actualizarContenido();
    }

    // ==========================================
    // PANEL DE DEVOLUCIONES
    // ==========================================

    private void mostrarPanelDevoluciones() {

        panelContenido.removeAll();

        PanelDevoluciones panelDevoluciones =
                new PanelDevoluciones(usuario);

        panelContenido.add(
                panelDevoluciones,
                BorderLayout.CENTER
        );

        actualizarContenido();
    }

    // ==========================================
    // MENSAJE DE ACCESO DENEGADO
    // ==========================================

    private void mostrarAccesoDenegado(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Acceso denegado",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ==========================================
    // ACTUALIZACIÓN DE LA INTERFAZ
    // ==========================================

    private void actualizarContenido() {

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ==========================================
    // CERRAR SESIÓN
    // ==========================================

    private void cerrarSesion() {

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {

            dispose();

            VentanaLogin login =
                    new VentanaLogin();

            login.setVisible(true);
        }
    }
}
