package main;

import dao.LibroDAO;
import modelo.Libro;

public class PruebaLibroDAO {

    public static void main(String[] args) {

        LibroDAO libroDAO = new LibroDAO();

        System.out.println("=== PRUEBA CRUD LIBRO ===");

        // CREATE
        Libro libroPrueba = new Libro(
                0,
                "Libro de Prueba EFT",
                "Autor Prueba",
                "ISBN-EFT-001",
                "Editorial Prueba",
                2,
                5
        );

        boolean insertado = libroDAO.insertar(libroPrueba);
        System.out.println("Insertar libro: " + insertado);

        // READ: buscamos el libro recién insertado por ISBN
        Libro encontrado = null;

        for (Libro libro : libroDAO.listar()) {
            if ("ISBN-EFT-001".equals(libro.getIsbn())) {
                encontrado = libro;
                break;
            }
        }

        if (encontrado == null) {
            System.out.println("No se encontró el libro de prueba.");
            return;
        }

        System.out.println(
                "Libro encontrado: " +
                        encontrado.getId() + " - " +
                        encontrado.getTitulo()
        );

        // UPDATE
        encontrado.setTitulo("Libro de Prueba EFT Modificado");
        encontrado.setStock(4);

        boolean actualizado = libroDAO.actualizar(encontrado);
        System.out.println("Actualizar libro: " + actualizado);

        Libro modificado = libroDAO.buscarPorId(encontrado.getId());

        if (modificado != null) {
            System.out.println(
                    "Después de actualizar: " +
                            modificado.getTitulo() +
                            " | Stock: " +
                            modificado.getStock()
            );
        }

        // DELETE
        boolean eliminado = libroDAO.eliminar(encontrado.getId());
        System.out.println("Eliminar libro: " + eliminado);

        Libro despuesDeEliminar =
                libroDAO.buscarPorId(encontrado.getId());

        System.out.println(
                "Existe después de eliminar: " +
                        (despuesDeEliminar != null)
        );
    }
}
