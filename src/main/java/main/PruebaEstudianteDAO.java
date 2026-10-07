package main;

import dao.EstudianteDAO;
import modelo.Estudiante;

public class PruebaEstudianteDAO {

    public static void main(String[] args) {

        EstudianteDAO estudianteDAO = new EstudianteDAO();

        System.out.println("=== PRUEBA CRUD ESTUDIANTE ===");

        // CREATE
        Estudiante estudiantePrueba = new Estudiante(
                0,
                "Estudiante Prueba EFT",
                "11111111-1",
                "4to Medio A",
                "pruebaeft@correo.cl"
        );

        boolean insertado = estudianteDAO.insertar(estudiantePrueba);
        System.out.println("Insertar estudiante: " + insertado);

        // READ
        Estudiante encontrado = null;

        for (Estudiante estudiante : estudianteDAO.listar()) {
            if ("11111111-1".equals(estudiante.getRut())) {
                encontrado = estudiante;
                break;
            }
        }

        if (encontrado == null) {
            System.out.println("No se encontró el estudiante de prueba.");
            return;
        }

        System.out.println(
                "Estudiante encontrado: "
                        + encontrado.getId() + " - "
                        + encontrado.getNombre()
        );

        // UPDATE
        encontrado.setNombre("Estudiante EFT Modificado");
        encontrado.setCurso("4to Medio B");

        boolean actualizado = estudianteDAO.actualizar(encontrado);
        System.out.println("Actualizar estudiante: " + actualizado);

        Estudiante modificado =
                estudianteDAO.buscarPorId(encontrado.getId());

        if (modificado != null) {
            System.out.println(
                    "Después de actualizar: "
                            + modificado.getNombre()
                            + " | Curso: "
                            + modificado.getCurso()
            );
        }

        // DELETE
        boolean eliminado =
                estudianteDAO.eliminar(encontrado.getId());

        System.out.println("Eliminar estudiante: " + eliminado);

        Estudiante despuesDeEliminar =
                estudianteDAO.buscarPorId(encontrado.getId());

        System.out.println(
                "Existe después de eliminar: "
                        + (despuesDeEliminar != null)
        );
    }
}