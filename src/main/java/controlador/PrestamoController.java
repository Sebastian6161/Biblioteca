
package controlador;

import dao.PrestamoDAO;
import modelo.Prestamo;
import modelo.Usuario;
import servicio.ServicioPrestamo;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.function.Consumer;

public class PrestamoController {

    private final ServicioPrestamo servicioPrestamo;
    private final PrestamoDAO prestamoDAO;

    public PrestamoController() {
        this.servicioPrestamo = new ServicioPrestamo();
        this.prestamoDAO = new PrestamoDAO();
    }

    // ==========================================
    // CONSULTAR PRÉSTAMOS ACTIVOS
    // ==========================================

    public List<Prestamo> listarPrestamosActivos() {
        return prestamoDAO.listarActivos();
    }

    // ==========================================
    // REGISTRAR PRÉSTAMO EN SEGUNDO PLANO
    // ==========================================

    public void registrarPrestamoAsync(
            int idEstudiante,
            int idLibro,
            Usuario usuario,
            Consumer<Boolean> callback
    ) {

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground() {
                        return servicioPrestamo.registrarPrestamo(
                                idEstudiante,
                                idLibro,
                                usuario
                        );
                    }

                    @Override
                    protected void done() {

                        try {
                            boolean resultado = get();

                            if (callback != null) {
                                callback.accept(resultado);
                            }

                        } catch (Exception e) {

                            System.err.println(
                                    "Error en el hilo de préstamo: "
                                            + e.getMessage()
                            );

                            if (callback != null) {
                                callback.accept(false);
                            }
                        }
                    }
                };

        worker.execute();
    }

    // ==========================================
    // REGISTRAR DEVOLUCIÓN EN SEGUNDO PLANO
    // ==========================================

    public void registrarDevolucionAsync(
            int idPrestamo,
            Usuario usuario,
            Consumer<Boolean> callback
    ) {

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground() {
                        return servicioPrestamo.registrarDevolucion(
                                idPrestamo,
                                usuario
                        );
                    }

                    @Override
                    protected void done() {

                        try {
                            boolean resultado = get();

                            if (callback != null) {
                                callback.accept(resultado);
                            }

                        } catch (Exception e) {

                            System.err.println(
                                    "Error en el hilo de devolución: "
                                            + e.getMessage()
                            );

                            if (callback != null) {
                                callback.accept(false);
                            }
                        }
                    }
                };

        worker.execute();
    }
}
