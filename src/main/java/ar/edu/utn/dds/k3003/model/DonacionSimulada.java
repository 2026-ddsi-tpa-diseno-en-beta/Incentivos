package ar.edu.utn.dds.k3003.model;

public class DonacionSimulada {
    private String categoria;
    private Integer cantidad;
    private boolean aceptada;

    public DonacionSimulada(String categoria, Integer cantidad, boolean aceptada) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.aceptada = aceptada;
    }

    public String getCategoria() { return categoria; }
    public Integer getCantidad(){return cantidad;}
    public boolean fueAceptada() { return aceptada; }
}
