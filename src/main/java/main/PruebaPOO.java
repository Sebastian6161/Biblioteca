package main;

import modelo.Estudiante;
import modelo.Libro;
import modelo.Persona;
import modelo.Usuario;

public class PruebaPOO {

    public static void main(String[] args) {

        Persona usuario = new Usuario(
                1,
                "Antonia Pérez",
                "12345678-9",
                "antonia@correo.cl",
                "clave123",
                "bibliotecario"
        );

        Persona estudiante = new Estudiante(
                1,
                "Carlos Ruiz",
                "98765432-1",
                "3ro Medio A",
                "carlos@correo.cl"
        );

        System.out.println(usuario.obtenerTipoPersona());
        System.out.println(estudiante.obtenerTipoPersona());

        Libro libro = new Libro(
                1,
                "Dune",
                "Frank Herbert",
                "9780441013593",
                "Ace Books",
                5,
                1
        );

        System.out.println("Stock inicial: " + libro.getStock());

        if (libro.tieneStockDisponible()) {
            libro.disminuirStock();
        }

        System.out.println("Stock después del préstamo: " + libro.getStock());

        libro.aumentarStock();

        System.out.println("Stock después de devolución: " + libro.getStock());
    }
}