package main;

import dao.PrestamoDAO;
import modelo.Prestamo;

import java.util.List;

public class PruebaPrestamoDAO {

    public static void main(String[] args) {

        PrestamoDAO prestamoDAO = new PrestamoDAO();

        List<Prestamo> prestamos = prestamoDAO.listar();

        System.out.println("=== PRÉSTAMOS REGISTRADOS ===");

        for (Prestamo prestamo : prestamos) {

            System.out.println(
                    "ID: " + prestamo.getId()
                            + " | Estudiante: " + prestamo.getIdEstudiante()
                            + " | Libro: " + prestamo.getIdLibro()
                            + " | Préstamo: " + prestamo.getFechaPrestamo()
                            + " | Devolución: " + prestamo.getFechaDevolucion()
                            + " | Devuelto: " + prestamo.isDevuelto()
                            + " | Atrasado: " + prestamo.estaAtrasado()
            );
        }

        System.out.println("Total: " + prestamos.size());
    }
}