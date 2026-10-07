package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;

public class LoginController {

    private final UsuarioDAO usuarioDAO;

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario iniciarSesion(String correo, String contrasena) {

        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            return null;
        }

        return usuarioDAO.autenticar(
                correo.trim(),
                contrasena
        );
    }
}