
package dao;

import modelo.Usuario;
import util.SeguridadContrasena;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Estudiante;

public class UsuarioDAO {

    public boolean insertar(Usuario usuario) {

        String sql = """
                INSERT INTO usuarios
                (nombre, rut, correo, `contraseña`, rol)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, SeguridadContrasena.generarHash(
                    usuario.getContrasena()
            ));
            ps.setString(5, usuario.getRol());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            System.err.println(
                    "Error al insertar usuario: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public List<Usuario> listar() {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuarios ORDER BY id";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                usuarios.add(
                        crearUsuarioDesdeResultSet(rs)
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al listar usuarios: "
                            + e.getMessage()
            );
        }

        return usuarios;
    }

    public Usuario buscarPorId(int id) {

        String sql =
                "SELECT * FROM usuarios WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return crearUsuarioDesdeResultSet(rs);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al buscar usuario: "
                            + e.getMessage()
            );
        }

        return null;
    }

    public boolean actualizar(Usuario usuario) {

        String sql = """
                UPDATE usuarios
                SET nombre = ?, rut = ?, correo = ?,
                    `contraseña` = ?, rol = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, SeguridadContrasena.generarHash(
                    usuario.getContrasena()
            ));
            ps.setString(5, usuario.getRol());
            ps.setInt(6, usuario.getId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar usuario: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM usuarios WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            System.err.println(
                    "Error al eliminar usuario: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public Usuario autenticar(
            String correo,
            String contrasena
    ) {

        String sql = """
                SELECT * FROM usuarios
                WHERE correo = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                Usuario usuario =
                        crearUsuarioDesdeResultSet(rs);

                String almacenada =
                        usuario.getContrasena();

                boolean valida;

                if (almacenada.startsWith("pbkdf2:")) {

                    valida = SeguridadContrasena.verificar(
                            contrasena,
                            almacenada
                    );

                } else {

                    // Compatibilidad con usuarios del script.
                    valida = almacenada.equals(contrasena);

                    if (valida) {
                        migrarContrasena(
                                conexion,
                                usuario.getId(),
                                contrasena
                        );
                    }
                }

                return valida ? usuario : null;
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al autenticar usuario: "
                            + e.getMessage()
            );

            return null;
        }
    }

    private void migrarContrasena(
            Connection conexion,
            int idUsuario,
            String contrasena
    ) throws SQLException {

        String sql = """
                UPDATE usuarios
                SET `contraseña` = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    SeguridadContrasena.generarHash(contrasena)
            );

            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario crearUsuarioDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                rs.getString("rol")
        );
    }

    // Inserción que participa en una transacción externa.
    // No cierra la conexión recibida.

    public boolean insertar(
            Usuario usuario,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO usuarios
                (nombre, rut, correo, `contraseña`, rol)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, SeguridadContrasena.generarHash(
                    usuario.getContrasena()
            ));
            ps.setString(5, usuario.getRol());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizarDatosPorRut(
            String rutOriginal,
            Estudiante estudiante,
            Connection conexion
    ) throws SQLException {

        String sql = """
            UPDATE usuarios
            SET nombre = ?,
                rut = ?,
                correo = ?
            WHERE rut = ?
              AND rol = 'estudiante'
            """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCorreo());
            ps.setString(4, rutOriginal);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminarEstudiantePorRut(
            String rut,
            Connection conexion
    ) throws SQLException {

        String sql = """
            DELETE FROM usuarios
            WHERE rut = ?
              AND rol = 'estudiante'
            """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, rut);

            return ps.executeUpdate() == 1;
        }
    }
}
