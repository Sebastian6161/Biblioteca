
package dao;

import modelo.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public boolean insertar(Libro libro) {

        String sql = """
                INSERT INTO libros
                (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al insertar libro: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public List<Libro> listar() {

        List<Libro> libros = new ArrayList<>();

        String sql = "SELECT * FROM libros ORDER BY id";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                );

                libros.add(libro);
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al listar libros: "
                            + e.getMessage()
            );
        }

        return libros;
    }

    public Libro buscarPorId(int id) {

        String sql = "SELECT * FROM libros WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Libro(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("isbn"),
                            rs.getString("editorial"),
                            rs.getInt("stock"),
                            rs.getInt("id_categoria")
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al buscar libro: "
                            + e.getMessage()
            );
        }

        return null;
    }

    public boolean actualizar(Libro libro) {

        String sql = """
                UPDATE libros
                SET titulo = ?, autor = ?, isbn = ?,
                    editorial = ?, stock = ?, id_categoria = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar libro: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql = "DELETE FROM libros WHERE id = ?";

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
                    "Error al eliminar libro: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean actualizarStock(
            int idLibro,
            int nuevoStock
    ) {

        if (nuevoStock < 0) {
            System.err.println(
                    "El stock no puede ser negativo."
            );
            return false;
        }

        String sql = """
                UPDATE libros
                SET stock = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = DatabaseConnection
                        .getInstance().getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, nuevoStock);
            ps.setInt(2, idLibro);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar stock: "
                            + e.getMessage()
            );

            return false;
        }
    }
}
