package main;

import dao.CategoriaDAO;
import modelo.Categoria;

import java.util.List;

public class PruebaCategoriaDAO {

    public static void main(String[] args) {

        CategoriaDAO categoriaDAO = new CategoriaDAO();

        List<Categoria> categorias = categoriaDAO.listar();

        System.out.println("=== CATEGORÍAS REGISTRADAS ===");

        for (Categoria categoria : categorias) {
            System.out.println(
                    categoria.getId() + " - " + categoria.getNombre()
            );
        }

        System.out.println("Total: " + categorias.size());
    }
}
