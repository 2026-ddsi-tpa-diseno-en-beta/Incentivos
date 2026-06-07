package ar.edu.utn.dds.k3003.model;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter

public class DonadorIncentivo {
    private String donadorId;
    private CategoriaDonadorEnum categoria= CategoriaDonadorEnum.OCASIONAL;
    private List<Insignia> insignias= new ArrayList<>();
    private List<Mision> misiones= new ArrayList<>();

    public DonadorIncentivo(String donadorId){
        this.donadorId= donadorId;
    }

    public String getDonadorId(){return donadorId;}
    public CategoriaDonadorEnum getCategoria(){return categoria;}
    public List<Insignia> getInsignias(){return insignias;}

    public void agregarInsignia(Insignia insignia){
        this.insignias.add(insignia);
    }

    public void asignarMision(Mision mision){
        this.misiones.add(mision);
    }

    public void avanzarCategoria(CategoriaDonadorEnum nuevaCategoria){
        this.categoria = nuevaCategoria;
    }

    public Mision getMisionActual(){
        return misiones.stream()
        .filter(m -> !m.estaCompletada())
        .findFirst()
        .orElse(null);
    }

    public void completarMisionActual(){
        Mision mision= getMisionActual();
        if(mision != null) {
        mision.completar();}
    }
}