package main;

import dao.UsuarioDAO;
import modelo.Usuario;

public class PruebaCRUDUsuario {

    public static void main(String[] args) {

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        System.out.println("=== PRUEBA CRUD USUARIO ===");

        // CREATE
        Usuario usuarioPrueba = new Usuario(
                0,
                "Usuario Prueba EFT",
                "22222222-2",
                "usuarioeft@correo.cl",
                "clavePrueba",
                "estudiante"
        );

        boolean insertado = usuarioDAO.insertar(usuarioPrueba);
        System.out.println("Insertar usuario: " + insertado);

        // READ
        Usuario encontrado = null;

        for (Usuario usuario : usuarioDAO.listar()) {
            if ("22222222-2".equals(usuario.getRut())) {
                encontrado = usuario;
                break;
            }
        }

        if (encontrado == null) {
            System.out.println("No se encontró el usuario de prueba.");
            return;
        }

        System.out.println(
                "Usuario encontrado: "
                        + encontrado.getId() + " - "
                        + encontrado.getNombre()
        );

        // UPDATE
        encontrado.setNombre("Usuario EFT Modificado");
        encontrado.setCorreo("usuario.modificado@correo.cl");

        boolean actualizado = usuarioDAO.actualizar(encontrado);
        System.out.println("Actualizar usuario: " + actualizado);

        Usuario modificado =
                usuarioDAO.buscarPorId(encontrado.getId());

        if (modificado != null) {
            System.out.println(
                    "Después de actualizar: "
                            + modificado.getNombre()
                            + " | "
                            + modificado.getCorreo()
            );
        }

        // DELETE
        boolean eliminado =
                usuarioDAO.eliminar(encontrado.getId());

        System.out.println("Eliminar usuario: " + eliminado);

        Usuario despuesDeEliminar =
                usuarioDAO.buscarPorId(encontrado.getId());

        System.out.println(
                "Existe después de eliminar: "
                        + (despuesDeEliminar != null)
        );
    }
}