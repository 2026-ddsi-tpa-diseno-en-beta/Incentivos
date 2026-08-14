package ar.edu.utn.dds.k3003.model;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter

@Entity
@Table(name="donadores")

public class DonadorIncentivo {
    @Id
    private String donadorId;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoria= CategoriaDonadorEnum.OCASIONAL;

    @ManyToMany
     @JoinTable(
        name = "donador_insignias",
        joinColumns = @JoinColumn(name = "donador_id"),
        inverseJoinColumns = @JoinColumn(name = "insignia_id")
    )
    private List<Insignia> insignias= new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "donador_misiones",
        joinColumns = @JoinColumn(name = "donador_id"),
        inverseJoinColumns = @JoinColumn(name = "mision_id")
    )
    private List<Mision> misiones= new ArrayList<>();

    public DonadorIncentivo() {}

    public DonadorIncentivo(String donadorId){
        this.donadorId= donadorId;
    }

    public String getDonadorId(){return donadorId;}
    public CategoriaDonadorEnum getCategoria(){return categoria;}
    public List<Insignia> getInsignias(){return insignias;}

    public void agregarInsignia(Insignia insignia){
        this.insignias.add(insignia);
    }

    public void quitarInsignia(String insigniaID){
    this.insignias.removeIf(
        insignia -> insignia.getId().equals(insigniaID)
    );
    }

    public void asignarMision(Mision mision){
        this.misiones.add(mision);
    }

    public void avanzarCategoria(CategoriaDonadorEnum nuevaCategoria){
        this.categoria = nuevaCategoria;
    }

    public void retrocederCategoria(CategoriaDonadorEnum categoriaAnterior) {
    this.categoria = categoriaAnterior;
    }

    public List<Mision> getMisiones(){
        return misiones;
    }

}