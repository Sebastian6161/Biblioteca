package main;

import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Libro;
import servicio.ServicioPrestamo;

public class PruebaServicioPrestamo {

    public static void main(String[] args) {

        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        ServicioPrestamo servicioPrestamo =
                new ServicioPrestamo();

        int idEstudiante = 1;
        int idLibro = 1;

        Libro antes = libroDAO.buscarPorId(idLibro);

        System.out.println("=== PRUEBA SERVICIO PRÉSTAMO ===");

        System.out.println(
                "Libro: " + antes.getTitulo()
        );

        System.out.println(
                "Stock antes: " + antes.getStock()
        );

        System.out.println(
                "Préstamos antes: "
                        + prestamoDAO.listar().size()
        );

        boolean resultado =
                servicioPrestamo.registrarPrestamo(
                        idEstudiante,
                        idLibro
                );

        System.out.println(
                "Resultado del préstamo: " + resultado
        );

        Libro despues =
                libroDAO.buscarPorId(idLibro);

        System.out.println(
                "Stock después: " + despues.getStock()
        );

        System.out.println(
                "Préstamos después: "
                        + prestamoDAO.listar().size()
        );
    }
}
