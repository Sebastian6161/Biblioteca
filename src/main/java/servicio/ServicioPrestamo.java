
package servicio;

import dao.DatabaseConnection;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ServicioPrestamo {

    // ==========================================
    // REGISTRAR PRÉSTAMO
    // ==========================================

    public synchronized boolean registrarPrestamo(
            int idEstudiante,
            int idLibro,
            Usuario usuario
    ) {

        if (!tieneRolValido(usuario)
                || idEstudiante <= 0
                || idLibro <= 0) {
            return false;
        }

        Connection conexion = null;

        try {
            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            conexion.setAutoCommit(false);

            // 1. Validar estudiante y permisos
            String sqlEstudiante = """
                    SELECT rut
                    FROM estudiantes
                    WHERE id = ?
                    """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlEstudiante)) {

                ps.setInt(1, idEstudiante);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        conexion.rollback();
                        return false;
                    }

                    String rutEstudiante = rs.getString("rut");

                    if (!tienePermisoSobreRut(
                            usuario,
                            rutEstudiante
                    )) {
                        conexion.rollback();
                        return false;
                    }
                }
            }

            // 2. Bloquear libro y consultar stock
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
                        conexion.rollback();
                        return false;
                    }

                    stockActual = rs.getInt("stock");
                }
            }

            if (stockActual <= 0) {
                System.err.println(
                        "El libro no tiene stock disponible."
                );

                conexion.rollback();
                return false;
            }

            // 3. Calcular fechas
            LocalDate fechaPrestamo = LocalDate.now();
            LocalDate fechaDevolucion =
                    fechaPrestamo.plusDays(7);

            // 4. Insertar préstamo
            String sqlPrestamo = """
                    INSERT INTO prestamos
                    (id_estudiante, id_libro,
                     fecha_prestamo, fecha_devolucion,
                     devuelto)
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

                if (ps.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }
            }

            // 5. Descontar una unidad del stock
            String sqlActualizarStock = """
                    UPDATE libros
                    SET stock = stock - 1
                    WHERE id = ?
                    AND stock > 0
                    """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(
                                 sqlActualizarStock
                         )) {

                ps.setInt(1, idLibro);

                if (ps.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }
            }

            // 6. Confirmar transacción
            conexion.commit();

            System.out.println(
                    "Préstamo registrado correctamente."
            );

            System.out.println(
                    "Fecha límite: " + fechaDevolucion
            );

            return true;

        } catch (SQLException e) {

            realizarRollback(conexion);

            System.err.println(
                    "Error al registrar préstamo: "
                            + e.getMessage()
            );

            return false;

        } finally {
            cerrarConexion(conexion);
        }
    }

    // ==========================================
    // REGISTRAR DEVOLUCIÓN
    // ==========================================

    public synchronized boolean registrarDevolucion(
            int idPrestamo,
            Usuario usuario
    ) {

        if (!tieneRolValido(usuario)
                || idPrestamo <= 0) {
            return false;
        }

        Connection conexion = null;

        try {
            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            conexion.setAutoCommit(false);

            // 1. Buscar préstamo y verificar titular
            String sqlPrestamo = """
                    SELECT
                        p.id_libro,
                        p.devuelto,
                        e.rut
                    FROM prestamos p
                    INNER JOIN estudiantes e
                        ON p.id_estudiante = e.id
                    WHERE p.id = ?
                    FOR UPDATE
                    """;

            int idLibro;
            boolean devuelto;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlPrestamo)) {

                ps.setInt(1, idPrestamo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        conexion.rollback();
                        return false;
                    }

                    idLibro = rs.getInt("id_libro");
                    devuelto = rs.getBoolean("devuelto");

                    String rutTitular = rs.getString("rut");

                    if (!tienePermisoSobreRut(
                            usuario,
                            rutTitular
                    )) {
                        conexion.rollback();
                        return false;
                    }
                }
            }

            // 2. Impedir devolución duplicada
            if (devuelto) {

                System.err.println(
                        "El préstamo ya fue devuelto."
                );

                conexion.rollback();
                return false;
            }

            // 3. Marcar préstamo como devuelto
            String sqlDevolucion = """
                    UPDATE prestamos
                    SET devuelto = TRUE
                    WHERE id = ?
                    AND devuelto = FALSE
                    """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlDevolucion)) {

                ps.setInt(1, idPrestamo);

                if (ps.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }
            }

            // 4. Recuperar unidad del stock
            String sqlStock = """
                    UPDATE libros
                    SET stock = stock + 1
                    WHERE id = ?
                    """;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlStock)) {

                ps.setInt(1, idLibro);

                if (ps.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }
            }

            // 5. Confirmar transacción
            conexion.commit();

            System.out.println(
                    "Devolución registrada correctamente."
            );

            return true;

        } catch (SQLException e) {

            realizarRollback(conexion);

            System.err.println(
                    "Error al registrar devolución: "
                            + e.getMessage()
            );

            return false;

        } finally {
            cerrarConexion(conexion);
        }
    }

    // ==========================================
    // VALIDACIÓN DE ROLES Y PERMISOS
    // ==========================================

    private boolean tieneRolValido(Usuario usuario) {

        if (usuario == null) {
            return false;
        }

        String rol = usuario.getRol();

        return "bibliotecario".equalsIgnoreCase(rol)
                || "estudiante".equalsIgnoreCase(rol);
    }

    private boolean tienePermisoSobreRut(
            Usuario usuario,
            String rutEstudiante
    ) {

        if (!tieneRolValido(usuario)
                || rutEstudiante == null) {
            return false;
        }

        if ("bibliotecario".equalsIgnoreCase(
                usuario.getRol()
        )) {
            return true;
        }

        return rutEstudiante.equals(usuario.getRut());
    }

    // ==========================================
    // MANEJO DE TRANSACCIONES
    // ==========================================

    private void realizarRollback(Connection conexion) {

        if (conexion == null) {
            return;
        }

        try {
            conexion.rollback();

        } catch (SQLException e) {

            System.err.println(
                    "Error al realizar rollback: "
                            + e.getMessage()
            );
        }
    }

    // ==========================================
    // CIERRE DE CONEXIÓN
    // ==========================================

    private void cerrarConexion(Connection conexion) {

        if (conexion == null) {
            return;
        }

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
