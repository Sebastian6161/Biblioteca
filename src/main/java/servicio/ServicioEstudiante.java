
package servicio;

import dao.DatabaseConnection;
import dao.EstudianteDAO;
import dao.UsuarioDAO;
import modelo.Estudiante;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import dao.PrestamoDAO;

public class ServicioEstudiante {

    private final EstudianteDAO estudianteDAO;
    private final UsuarioDAO usuarioDAO;

    public ServicioEstudiante() {
        estudianteDAO = new EstudianteDAO();
        usuarioDAO = new UsuarioDAO();
    }

    public boolean registrarEstudiante(
            Estudiante estudiante,
            String contrasena
    ) {

        if (estudiante == null
                || contrasena == null
                || contrasena.isBlank()) {

            return false;
        }

        Connection conexion = null;
        boolean autoCommitOriginal = true;

        try {

            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            autoCommitOriginal = conexion.getAutoCommit();

            conexion.setAutoCommit(false);

            boolean estudianteGuardado =
                    estudianteDAO.insertar(
                            estudiante,
                            conexion
                    );

            if (!estudianteGuardado) {
                conexion.rollback();
                return false;
            }

            Usuario usuario = new Usuario(
                    0,
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCorreo(),
                    contrasena,
                    "estudiante"
            );

            boolean usuarioGuardado =
                    usuarioDAO.insertar(
                            usuario,
                            conexion
                    );

            if (!usuarioGuardado) {
                conexion.rollback();
                return false;
            }

            conexion.commit();

            System.out.println(
                    "Estudiante y usuario registrados correctamente."
            );

            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Error al registrar estudiante: "
                            + e.getMessage()
            );

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.err.println(
                            "Error al revertir transacción: "
                                    + ex.getMessage()
                    );
                }
            }

            return false;

        } finally {

            if (conexion != null) {

                try {
                    conexion.setAutoCommit(autoCommitOriginal);
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

    public boolean actualizarEstudiante(
            Estudiante estudiante,
            String rutOriginal
    ) {

        if (estudiante == null
                || estudiante.getId() <= 0
                || rutOriginal == null
                || rutOriginal.isBlank()
                || estudiante.getNombre() == null
                || estudiante.getNombre().isBlank()
                || estudiante.getRut() == null
                || estudiante.getRut().isBlank()
                || estudiante.getCurso() == null
                || estudiante.getCurso().isBlank()
                || estudiante.getCorreo() == null
                || estudiante.getCorreo().isBlank()) {

            return false;
        }

        Connection conexion = null;

        try {
            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            conexion.setAutoCommit(false);

            boolean estudianteActualizado =
                    estudianteDAO.actualizar(
                            estudiante,
                            conexion
                    );

            if (!estudianteActualizado) {
                conexion.rollback();
                return false;
            }

            boolean usuarioActualizado =
                    usuarioDAO.actualizarDatosPorRut(
                            rutOriginal,
                            estudiante,
                            conexion
                    );

            if (!usuarioActualizado) {
                conexion.rollback();
                return false;
            }

            conexion.commit();

            System.out.println(
                    "Estudiante y usuario actualizados correctamente."
            );

            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar estudiante: "
                            + e.getMessage()
            );

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.err.println(
                            "Error al revertir transacción: "
                                    + ex.getMessage()
                    );
                }
            }

            return false;

        } finally {

            if (conexion != null) {
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

    public boolean eliminarEstudiante(int idEstudiante) {

        if (idEstudiante <= 0) {
            return false;
        }

        Connection conexion = null;

        try {
            conexion = DatabaseConnection
                    .getInstance()
                    .getConnection();

            conexion.setAutoCommit(false);

            // Obtenemos los datos originales del estudiante.
            String rutEstudiante;

            String sql = """
                SELECT rut
                FROM estudiantes
                WHERE id = ?
                FOR UPDATE
                """;

            try (java.sql.PreparedStatement ps =
                         conexion.prepareStatement(sql)) {

                ps.setInt(1, idEstudiante);

                try (java.sql.ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        conexion.rollback();
                        return false;
                    }

                    rutEstudiante = rs.getString("rut");
                }
            }

            // No eliminamos estudiantes con historial de préstamos.
            PrestamoDAO prestamoDAO = new PrestamoDAO();

            if (prestamoDAO.tienePrestamos(
                    idEstudiante,
                    conexion
            )) {
                conexion.rollback();

                System.out.println(
                        "No se puede eliminar: "
                                + "el estudiante tiene préstamos registrados."
                );

                return false;
            }

            // Eliminamos primero la cuenta asociada.
            boolean usuarioEliminado =
                    usuarioDAO.eliminarEstudiantePorRut(
                            rutEstudiante,
                            conexion
                    );

            if (!usuarioEliminado) {
                conexion.rollback();
                return false;
            }

            // Eliminamos el registro del estudiante.
            boolean estudianteEliminado =
                    estudianteDAO.eliminar(
                            idEstudiante,
                            conexion
                    );

            if (!estudianteEliminado) {
                conexion.rollback();
                return false;
            }

            conexion.commit();

            System.out.println(
                    "Estudiante y cuenta eliminados correctamente."
            );

            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Error al eliminar estudiante: "
                            + e.getMessage()
            );

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.err.println(
                            "Error al revertir eliminación: "
                                    + ex.getMessage()
                    );
                }
            }

            return false;

        } finally {

            if (conexion != null) {
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
