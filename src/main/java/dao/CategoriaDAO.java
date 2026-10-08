
package dao;

import modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean insertar(Categoria categoria) {

        String sql =
                "INSERT INTO categorias (nombre) VALUES (?)";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, categoria.getNombre());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al insertar categoría: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public List<Categoria> listar() {

        List<Categoria> categorias = new ArrayList<>();

        String sql =
                "SELECT * FROM categorias ORDER BY id";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Categoria categoria = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );

                categorias.add(categoria);
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al listar categorías: "
                            + e.getMessage()
            );
        }

        return categorias;
    }

    public boolean actualizar(Categoria categoria) {

        String sql = """
                UPDATE categorias
                SET nombre = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar categoría: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM categorias WHERE id = ?";

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
                    "Error al eliminar categoría: "
                            + e.getMessage()
            );

            return false;
        }
    }
}
