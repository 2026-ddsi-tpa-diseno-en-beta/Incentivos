package ar.edu.utn.dds.k3003.model;


import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name="misiones")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_mision")
public abstract class Mision {
    @Id
    private String id;
    private String nombre;
    private String insigniaID;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoriaInicio;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoriaFin;
     private boolean completada= false;

    @Enumerated(EnumType.STRING)
    private TipoMisionEnum tipo;

        protected Mision() {}

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
    



    public String getId(){return id;};
    public String getNombre() { return nombre; }
    public String getInsigniaID() { return insigniaID; }
    public CategoriaDonadorEnum getCategoriaInicio() { return categoriaInicio; }
    public CategoriaDonadorEnum getCategoriaFin() { return categoriaFin; }
    public TipoMisionEnum getTipo(){ return tipo;}

    @PrePersist
private void generarId() {
    if(id == null){
        id = UUID.randomUUID().toString();
    }
} 
}