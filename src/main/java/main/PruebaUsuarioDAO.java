package main;

import dao.UsuarioDAO;
import modelo.Usuario;

public class PruebaUsuarioDAO {

    public static void main(String[] args) {

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        System.out.println("=== PRUEBA AUTENTICACIÓN ===");

        Usuario correcto = usuarioDAO.autenticar(
                "antonia@correo.cl",
                "clave123"
        );

        if (correcto != null) {
            System.out.println("Login correcto");
            System.out.println("Usuario: " + correcto.getNombre());
            System.out.println("Rol: " + correcto.getRol());
        } else {
            System.out.println("Error: usuario no encontrado");
        }

        System.out.println();

        Usuario incorrecto = usuarioDAO.autenticar(
                "antonia@correo.cl",
                "contraseñaIncorrecta"
        );

        if (incorrecto == null) {
            System.out.println("Login incorrecto rechazado correctamente");
        } else {
            System.out.println("ERROR: se aceptaron credenciales incorrectas");
        }
    }
}