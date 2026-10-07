package controlador;

import dao.LibroDAO;
import modelo.Libro;
import dao.CategoriaDAO;
import modelo.Categoria;

import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;


    public LibroController() {
        this.libroDAO = new LibroDAO();
        this.categoriaDAO = new CategoriaDAO();
    }

    public List<Libro> listarLibros() {
        return libroDAO.listar();
    }

    public Libro buscarPorId(int id) {
        return libroDAO.buscarPorId(id);
    }

    public boolean guardar(Libro libro) {

        if (libro == null) {
            return false;
        }

        if (libro.getTitulo() == null
                || libro.getTitulo().trim().isEmpty()) {
            return false;
        }

        if (libro.getAutor() == null
                || libro.getAutor().trim().isEmpty()) {
            return false;
        }

        if (libro.getIsbn() == null
                || libro.getIsbn().trim().isEmpty()) {
            return false;
        }

        if (libro.getStock() < 0) {
            return false;
        }

        return libroDAO.insertar(libro);
    }

    public boolean actualizar(Libro libro) {

        if (libro == null || libro.getId() <= 0) {
            return false;
        }

        if (libro.getTitulo() == null
                || libro.getTitulo().trim().isEmpty()) {
            return false;
        }

        if (libro.getStock() < 0) {
            return false;
        }

        return libroDAO.actualizar(libro);
    }

    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        return libroDAO.eliminar(id);
    }
    public String obtenerNombreCategoria(int idCategoria) {

        List<Categoria> categorias =
                categoriaDAO.listar();

        for (Categoria categoria : categorias) {

            if (categoria.getId() == idCategoria) {
                return categoria.getNombre();
            }
        }

        return "Sin categoría";
    }
}