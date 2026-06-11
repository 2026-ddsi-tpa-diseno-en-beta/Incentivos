package ar.edu.utn.dds.k3003.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="insignias")
public class Insignia {
    @Id
    private String id;

    private String nombre;
    private String descripcion;
    
    public Insignia() {}

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

    @PrePersist
private void generarId() {
    if(id == null){
        id = UUID.randomUUID().toString();
    }
}
    
}
