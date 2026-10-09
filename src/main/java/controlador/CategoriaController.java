
package controlador;

import dao.CategoriaDAO;
import dao.DatabaseConnection;
import modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    // ==========================================
    // LISTAR CATEGORÍAS
    // ==========================================

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    // ==========================================
    // REGISTRAR CATEGORÍA
    // ==========================================

    public boolean guardar(String nombre) {

        if (!nombreValido(nombre)) {
            return false;
        }

        String nombreLimpio = nombre.trim();

        if (existeNombre(nombreLimpio, 0)) {
            return false;
        }

        Categoria categoria = new Categoria(
                0,
                nombreLimpio
        );

        return categoriaDAO.insertar(categoria);
    }

    // ==========================================
    // ACTUALIZAR CATEGORÍA
    // ==========================================

    public boolean actualizar(
            int id,
            String nombre
    ) {

        if (id <= 0 || !nombreValido(nombre)) {
            return false;
        }

        String nombreLimpio = nombre.trim();

        if (existeNombre(nombreLimpio, id)) {
            return false;
        }

        Categoria categoria = new Categoria(
                id,
                nombreLimpio
        );

        return categoriaDAO.actualizar(categoria);
    }

    // ==========================================
    // ELIMINAR CATEGORÍA
    // ==========================================

    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        if (tieneLibrosAsociados(id)) {

            System.err.println(
                    "No se puede eliminar la categoría "
                            + "porque tiene libros asociados."
            );

            return false;
        }

        return categoriaDAO.eliminar(id);
    }

    // ==========================================
    // VALIDAR NOMBRE
    // ==========================================

    private boolean nombreValido(String nombre) {

        return nombre != null
                && !nombre.trim().isEmpty()
                && nombre.trim().length() <= 100;
    }

    // ==========================================
    // COMPROBAR NOMBRE DUPLICADO
    // ==========================================

    private boolean existeNombre(
            String nombre,
            int idExcluir
    ) {

        String sql = """
                SELECT COUNT(*) AS total
                FROM categorias
                WHERE LOWER(nombre) = LOWER(?)
                  AND id <> ?
                """;

        try (
                Connection conexion =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, nombre);
            ps.setInt(2, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al validar nombre de categoría: "
                            + e.getMessage()
            );

            // Ante un error de consulta,
            // evitar asumir que el nombre está libre.
            return true;
        }

        return false;
    }

    // ==========================================
    // COMPROBAR LIBROS ASOCIADOS
    // ==========================================

    public boolean tieneLibrosAsociados(int idCategoria) {

        String sql = """
                SELECT COUNT(*) AS total
                FROM libros
                WHERE id_categoria = ?
                """;

        try (
                Connection conexion =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al comprobar libros asociados: "
                            + e.getMessage()
            );

            // Si falla la consulta, bloquear eliminación.
            return true;
        }

        return false;
    }
}
