package controlador;

import servicio.ServicioPrestamo;

import javax.swing.SwingWorker;
import java.util.function.Consumer;

public class PrestamoController {

    private final ServicioPrestamo servicioPrestamo;

    public PrestamoController() {
        this.servicioPrestamo = new ServicioPrestamo();
    }

    public void registrarPrestamoAsync(
            int idEstudiante,
            int idLibro,
            Consumer<Boolean> callback
    ) {

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground() {
                        return servicioPrestamo.registrarPrestamo(
                                idEstudiante,
                                idLibro
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

    public void registrarDevolucionAsync(
            int idPrestamo,
            Consumer<Boolean> callback
    ) {

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground() {
                        return servicioPrestamo.registrarDevolucion(
                                idPrestamo
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