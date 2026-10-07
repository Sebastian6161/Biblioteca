package main;

import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Libro;
import modelo.Prestamo;
import servicio.ServicioPrestamo;

public class PruebaServicioDevolucion {

    public static void main(String[] args) {

        ServicioPrestamo servicioPrestamo =
                new ServicioPrestamo();

        PrestamoDAO prestamoDAO =
                new PrestamoDAO();

        LibroDAO libroDAO =
                new LibroDAO();

        int idPrestamo = 11;

        Prestamo antes =
                prestamoDAO.buscarPorId(idPrestamo);

        if (antes == null) {
            System.out.println(
                    "No existe el préstamo " + idPrestamo
            );
            return;
        }

        Libro libro =
                libroDAO.buscarPorId(antes.getIdLibro());

        System.out.println("=== PRUEBA DEVOLUCIÓN ===");

        System.out.println(
                "Préstamo ID: " + antes.getId()
        );

        System.out.println(
                "Devuelto antes: " + antes.isDevuelto()
        );

        System.out.println(
                "Stock antes: " + libro.getStock()
        );

        boolean resultado =
                servicioPrestamo.registrarDevolucion(
                        idPrestamo
                );

        System.out.println(
                "Resultado devolución: " + resultado
        );

        Prestamo despues =
                prestamoDAO.buscarPorId(idPrestamo);

        Libro libroDespues =
                libroDAO.buscarPorId(antes.getIdLibro());

        System.out.println(
                "Devuelto después: "
                        + despues.isDevuelto()
        );

        System.out.println(
                "Stock después: "
                        + libroDespues.getStock()
        );
    }
}