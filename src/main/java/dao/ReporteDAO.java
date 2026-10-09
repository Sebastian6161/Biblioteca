
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // ==========================================
    // LIBROS MÁS PRESTADOS
    // ==========================================

    public List<Object[]> librosMasPrestados() {

        List<Object[]> resultados = new ArrayList<>();

        String sql = """
                SELECT
                    l.titulo,
                    l.autor,
                    COUNT(p.id) AS total_prestamos
                FROM libros l
                LEFT JOIN prestamos p
                    ON l.id = p.id_libro
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY total_prestamos DESC, l.titulo
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance()
                             .getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                resultados.add(new Object[]{
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("total_prestamos")
                });
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error en reporte de libros: "
                            + e.getMessage()
            );
        }

        return resultados;
    }

    // ==========================================
    // HISTORIAL DE UN ESTUDIANTE
    // ==========================================

    public List<Object[]> historialEstudiante(
            int idEstudiante
    ) {

        List<Object[]> resultados = new ArrayList<>();

        String sql = """
                SELECT
                    l.titulo,
                    p.fecha_prestamo,
                    p.fecha_devolucion,
                    CASE
                        WHEN p.devuelto = TRUE
                            THEN 'Devuelto'
                        WHEN p.fecha_devolucion < CURDATE()
                            THEN 'Atrasado'
                        ELSE 'Pendiente'
                    END AS estado
                FROM prestamos p
                INNER JOIN libros l
                    ON p.id_libro = l.id
                WHERE p.id_estudiante = ?
                ORDER BY p.fecha_prestamo DESC, p.id DESC
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance()
                             .getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    resultados.add(new Object[]{
                            rs.getString("titulo"),
                            rs.getDate("fecha_prestamo"),
                            rs.getDate("fecha_devolucion"),
                            rs.getString("estado")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error en historial: "
                            + e.getMessage()
            );
        }

        return resultados;
    }

    // ==========================================
    // PRÉSTAMOS ACTUALMENTE ACTIVOS
    // ==========================================

    public List<Object[]> prestamosActivos() {

        List<Object[]> resultados = new ArrayList<>();

        String sql = """
                SELECT
                    e.nombre AS estudiante,
                    l.titulo AS libro,
                    p.fecha_prestamo,
                    p.fecha_devolucion
                FROM prestamos p
                INNER JOIN estudiantes e
                    ON p.id_estudiante = e.id
                INNER JOIN libros l
                    ON p.id_libro = l.id
                WHERE p.devuelto = FALSE
                ORDER BY p.fecha_devolucion ASC
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance()
                             .getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                resultados.add(new Object[]{
                        rs.getString("estudiante"),
                        rs.getString("libro"),
                        rs.getDate("fecha_prestamo"),
                        rs.getDate("fecha_devolucion")
                });
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error en préstamos activos: "
                            + e.getMessage()
            );
        }

        return resultados;
    }

    // ==========================================
    // PRÉSTAMOS ATRASADOS
    // ==========================================

    public List<Object[]> prestamosAtrasados() {

        List<Object[]> resultados = new ArrayList<>();

        String sql = """
                SELECT
                    e.nombre AS estudiante,
                    l.titulo AS libro,
                    p.fecha_devolucion,
                    DATEDIFF(
                        CURDATE(),
                        p.fecha_devolucion
                    ) AS dias_atraso
                FROM prestamos p
                INNER JOIN estudiantes e
                    ON p.id_estudiante = e.id
                INNER JOIN libros l
                    ON p.id_libro = l.id
                WHERE p.devuelto = FALSE
                  AND p.fecha_devolucion < CURDATE()
                ORDER BY dias_atraso DESC
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance()
                             .getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                resultados.add(new Object[]{
                        rs.getString("estudiante"),
                        rs.getString("libro"),
                        rs.getDate("fecha_devolucion"),
                        rs.getInt("dias_atraso")
                });
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error en préstamos atrasados: "
                            + e.getMessage()
            );
        }

        return resultados;
    }
}
