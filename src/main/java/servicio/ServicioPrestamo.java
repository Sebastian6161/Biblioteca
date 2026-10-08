package servicio;

import dao.DatabaseConnection;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ServicioPrestamo {

    public synchronized boolean registrarPrestamo(
            int idEstudiante,
            int idLibro
    ) {

        Connection conexion = null;

        try {
            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            conexion.setAutoCommit(false);

            // 1. Consultar y bloquear el libro durante la operación
            String sqlStock = """
                    SELECT stock
                    FROM libros
                    WHERE id = ?
                    FOR UPDATE
                    """;

            int stockActual;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlStock)) {

                ps.setInt(1, idLibro);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        System.err.println("El libro no existe.");
                        conexion.rollback();
                        return false;
                    }

                    stockActual = rs.getInt("stock");
                }
            }

            // 2. Comprobar disponibilidad
            if (stockActual <= 0) {
                System.err.println("El libro no tiene stock disponible.");
                conexion.rollback();
                return false;
            }

            // 3. Calcular fechas automáticamente
            LocalDate fechaPrestamo = LocalDate.now();
            LocalDate fechaDevolucion = fechaPrestamo.plusDays(7);

            // 4. Registrar préstamo
            String sqlPrestamo = """
                    INSERT INTO prestamos
                    (id_estudiante, id_libro, fecha_prestamo,
                     fecha_devolucion, devuelto)
                    VALUES (?, ?, ?, ?, FALSE)
                    """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlPrestamo)) {

                ps.setInt(1, idEstudiante);
                ps.setInt(2, idLibro);
                ps.setDate(
                        3,
                        java.sql.Date.valueOf(fechaPrestamo)
                );
                ps.setDate(
                        4,
                        java.sql.Date.valueOf(fechaDevolucion)
                );

                ps.executeUpdate();
            }

            // 5. Disminuir stock en MySQL
            String sqlActualizarStock = """
                    UPDATE libros
                    SET stock = stock - 1
                    WHERE id = ?
                    """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlActualizarStock)) {

                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            // 6. Confirmar ambas operaciones
            conexion.commit();

            System.out.println(
                    "Préstamo registrado correctamente."
            );

            System.out.println(
                    "Fecha límite de devolución: "
                            + fechaDevolucion
            );

            return true;

        } catch (SQLException e) {

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(
                            "Error al realizar rollback: "
                                    + rollbackError.getMessage()
                    );
                }
            }

            System.err.println(
                    "Error al registrar préstamo: "
                            + e.getMessage()
            );

            return false;

        } finally {

            if (conexion != null) {

                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println(
                            "Error al restaurar AutoCommit: "
                                    + e.getMessage()
                    );
                }

                try {
                    conexion.close();
                } catch (SQLException e) {
                    System.err.println(
                            "Error al cerrar conexión: "
                                    + e.getMessage()
                    );
                }
            }
        }
    }
    public synchronized boolean registrarDevolucion(int idPrestamo) {

        Connection conexion = null;

        try {
            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            conexion.setAutoCommit(false);

            // 1. Buscar y bloquear el préstamo
            String sqlPrestamo = """
                SELECT id_libro, devuelto
                FROM prestamos
                WHERE id = ?
                FOR UPDATE
                """;

            int idLibro;
            boolean devuelto;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlPrestamo)) {

                ps.setInt(1, idPrestamo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        System.err.println("El préstamo no existe.");
                        conexion.rollback();
                        return false;
                    }

                    idLibro = rs.getInt("id_libro");
                    devuelto = rs.getBoolean("devuelto");
                }
            }

            // 2. Evitar devolver dos veces el mismo préstamo
            if (devuelto) {
                System.err.println(
                        "El préstamo ya había sido devuelto."
                );
                conexion.rollback();
                return false;
            }

            // 3. Marcar préstamo como devuelto
            String sqlDevolucion = """
                UPDATE prestamos
                SET devuelto = TRUE
                WHERE id = ?
                """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlDevolucion)) {

                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }

            // 4. Devolver la unidad al stock
            String sqlStock = """
                UPDATE libros
                SET stock = stock + 1
                WHERE id = ?
                """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlStock)) {

                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            // 5. Confirmar las dos operaciones
            conexion.commit();

            System.out.println(
                    "Devolución registrada correctamente."
            );

            return true;

        } catch (SQLException e) {

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(
                            "Error al realizar rollback: "
                                    + rollbackError.getMessage()
                    );
                }
            }

            System.err.println(
                    "Error al registrar devolución: "
                            + e.getMessage()
            );

            return false;

        } finally {

            if (conexion != null) {

                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println(
                            "Error al restaurar AutoCommit: "
                                    + e.getMessage()
                    );
                }

                try {
                    conexion.close();
                } catch (SQLException e) {
                    System.err.println(
                            "Error al cerrar conexión: "
                                    + e.getMessage()
                    );
                }
            }
        }
    }
}

