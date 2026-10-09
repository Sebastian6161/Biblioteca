
package controlador;

import dao.ReporteDAO;

import java.util.List;

public class ReporteController {

    private final ReporteDAO reporteDAO;

    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
    }

    // ==========================================
    // LIBROS MÁS PRESTADOS
    // ==========================================

    public List<Object[]> obtenerLibrosMasPrestados() {
        return reporteDAO.librosMasPrestados();
    }

    // ==========================================
    // HISTORIAL POR ESTUDIANTE
    // ==========================================

    public List<Object[]> obtenerHistorialEstudiante(
            int idEstudiante
    ) {
        if (idEstudiante <= 0) {
            return List.of();
        }

        return reporteDAO.historialEstudiante(
                idEstudiante
        );
    }

    // ==========================================
    // PRÉSTAMOS ACTIVOS
    // ==========================================

    public List<Object[]> obtenerPrestamosActivos() {
        return reporteDAO.prestamosActivos();
    }

    // ==========================================
    // PRÉSTAMOS ATRASADOS
    // ==========================================

    public List<Object[]> obtenerPrestamosAtrasados() {
        return reporteDAO.prestamosAtrasados();
    }
}
