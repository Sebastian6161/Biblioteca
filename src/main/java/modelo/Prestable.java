package modelo;

public interface Prestable {

    boolean tieneStockDisponible();

    void disminuirStock();

    void aumentarStock();
}