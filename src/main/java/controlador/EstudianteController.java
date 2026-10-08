
package controlador;

import dao.EstudianteDAO;
import modelo.Estudiante;
import servicio.ServicioEstudiante;

import java.util.List;

public class EstudianteController {

    private final EstudianteDAO estudianteDAO;
    private final ServicioEstudiante servicioEstudiante;

    public EstudianteController() {
        this.estudianteDAO = new EstudianteDAO();
        this.servicioEstudiante = new ServicioEstudiante();
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteDAO.listar();
    }

    public Estudiante buscarPorId(int id) {
        if (id <= 0) {
            return null;
        }

        return estudianteDAO.buscarPorId(id);
    }

    public boolean guardar(Estudiante estudiante) {

        if (!validarEstudiante(estudiante)) {
            return false;
        }

        return estudianteDAO.insertar(estudiante);
    }

    public boolean actualizar(Estudiante estudiante) {

        if (!validarEstudiante(estudiante)
                || estudiante.getId() <= 0) {
            return false;
        }

        return estudianteDAO.actualizar(estudiante);
    }


    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        return servicioEstudiante.eliminarEstudiante(id);
    }

    private boolean validarEstudiante(Estudiante estudiante) {

        if (estudiante == null) {
            return false;
        }

        if (estudiante.getNombre() == null
                || estudiante.getNombre().trim().isEmpty()) {
            return false;
        }

        if (estudiante.getRut() == null
                || estudiante.getRut().trim().isEmpty()) {
            return false;
        }

        if (estudiante.getCurso() == null
                || estudiante.getCurso().trim().isEmpty()) {
            return false;
        }

        if (estudiante.getCorreo() == null
                || estudiante.getCorreo().trim().isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean registrarConCuenta(
            Estudiante estudiante,
            String contrasena
    ) {

        if (!validarEstudiante(estudiante)) {
            return false;
        }

        if (contrasena == null
                || contrasena.trim().isEmpty()) {
            return false;
        }

        return servicioEstudiante.registrarEstudiante(
                estudiante,
                contrasena
        );
    }


    public boolean actualizarConCuenta(
            Estudiante estudiante,
            String rutOriginal
    ) {

        if (!validarEstudiante(estudiante)
                || estudiante.getId() <= 0
                || rutOriginal == null
                || rutOriginal.isBlank()) {
            return false;
        }

        return servicioEstudiante.actualizarEstudiante(
                estudiante,
                rutOriginal
        );
    }
}
