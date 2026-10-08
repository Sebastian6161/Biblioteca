
package dao;

import modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public boolean insertar(Estudiante estudiante) {

        String sql = """
                INSERT INTO estudiantes
                (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al insertar estudiante: "
                            + e.getMessage()
            );
            return false;
        }
    }

    public List<Estudiante> listar() {

        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes ORDER BY id";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Estudiante estudiante = new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("curso"),
                        rs.getString("correo")
                );

                estudiantes.add(estudiante);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al listar estudiantes: "
                            + e.getMessage()
            );
        }

        return estudiantes;
    }

    public Estudiante buscarPorId(int id) {

        String sql =
                "SELECT * FROM estudiantes WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Estudiante(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("curso"),
                            rs.getString("correo")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al buscar estudiante: "
                            + e.getMessage()
            );
        }

        return null;
    }

    public boolean actualizar(Estudiante estudiante) {

        String sql = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al actualizar estudiante: "
                            + e.getMessage()
            );
            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM estudiantes WHERE id = ?";

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
                    "Error al eliminar estudiante: "
                            + e.getMessage()
            );
            return false;
        }
    }

    // Este método participa en una transacción externa.
    // NO debe cerrar la conexión recibida.

    public boolean insertar(
            Estudiante estudiante,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO estudiantes
                (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(
            Estudiante estudiante,
            Connection conexion
    ) throws SQLException {

        String sql = """
            UPDATE estudiantes
            SET nombre = ?,
                rut = ?,
                curso = ?,
                correo = ?
            WHERE id = ?
            """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(
            int id,
            Connection conexion
    ) throws SQLException {

        String sql = """
            DELETE FROM estudiantes
            WHERE id = ?
            """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }

}
