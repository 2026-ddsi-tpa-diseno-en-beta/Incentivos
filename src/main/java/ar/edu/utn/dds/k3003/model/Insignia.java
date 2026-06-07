package ar.edu.utn.dds.k3003.model;

public class Insignia {
    private String id;
    private String nombre;
    private String descripcion;
    
    public Insignia(String id, String nombre, String descripcion){
        this.id = id;
        this.nombre = nombre;
        this.descripcion= descripcion;
    }

    public String getNombre(){
        return nombre;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public String getId(){
        return id;
    }
    
}
