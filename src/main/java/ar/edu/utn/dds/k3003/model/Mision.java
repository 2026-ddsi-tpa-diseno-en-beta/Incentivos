package ar.edu.utn.dds.k3003.model;


import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;

import java.util.List;



public abstract class Mision {
    private String id;
    private String nombre;
    private String insigniaID;
    private CategoriaDonadorEnum categoriaInicio;
    private CategoriaDonadorEnum categoriaFin;
    private boolean completada= false;
    private TipoMisionEnum tipo;

        public Mision(String id, String nombre, String insigniaID,
                  CategoriaDonadorEnum inicio,
                  CategoriaDonadorEnum fin, TipoMisionEnum tipo) {
        this.id = id;
        this.nombre = nombre;
        this.insigniaID = insigniaID;
        this.categoriaInicio = inicio;
        this.categoriaFin = fin;
        this.tipo= tipo;
    }

        public abstract boolean estaCompleta(List<DonacionSimulada> donaciones);
    

    public boolean estaCompletada() { return completada; }

    public void completar() { 
        this.completada = true; 
    }
     public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getId(){return id;};
    public String getNombre() { return nombre; }
    public String getInsigniaID() { return insigniaID; }
    public CategoriaDonadorEnum getCategoriaInicio() { return categoriaInicio; }
    public CategoriaDonadorEnum getCategoriaFin() { return categoriaFin; }
    public TipoMisionEnum getTipo(){ return tipo;}
}
