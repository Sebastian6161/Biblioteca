
package dao;

import modelo.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public boolean insertar(Prestamo prestamo) {

        String sql = """
                INSERT INTO prestamos
                (id_estudiante, id_libro, fecha_prestamo,
                 fecha_devolucion, devuelto)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setBoolean(5, prestamo.isDevuelto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al insertar préstamo: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public List<Prestamo> listar() {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = "SELECT * FROM prestamos ORDER BY id";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                prestamos.add(
                        crearPrestamoDesdeResultSet(rs)
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al listar préstamos: "
                            + e.getMessage()
            );
        }

        return prestamos;
    }

    public Prestamo buscarPorId(int id) {

        String sql =
                "SELECT * FROM prestamos WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return crearPrestamoDesdeResultSet(rs);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al buscar préstamo: "
                            + e.getMessage()
            );
        }

        return null;
    }

    public boolean actualizar(Prestamo prestamo) {

        String sql = """
                UPDATE prestamos
                SET id_estudiante = ?,
                    id_libro = ?,
                    fecha_prestamo = ?,
                    fecha_devolucion = ?,
                    devuelto = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.setInt(6, prestamo.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar préstamo: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM prestamos WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al eliminar préstamo: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public List<Prestamo> listarActivos() {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT * FROM prestamos
                WHERE devuelto = FALSE
                ORDER BY fecha_devolucion
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                prestamos.add(
                        crearPrestamoDesdeResultSet(rs)
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al listar préstamos activos: "
                            + e.getMessage()
            );
        }

        return prestamos;
    }

    private Prestamo crearPrestamoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion").toLocalDate(),
                rs.getBoolean("devuelto")
        );
    }

    public boolean tienePrestamos(
            int idEstudiante,
            Connection conexion
    ) throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM prestamos
            WHERE id_estudiante = ?
            """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}
