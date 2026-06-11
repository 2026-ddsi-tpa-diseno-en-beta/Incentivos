package ar.edu.utn.dds.k3003.model;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DONACIONES_EXITOSAS")
public class MisionDonacionesExitosas extends Mision{
     public MisionDonacionesExitosas() {
    super();
}
    
    public MisionDonacionesExitosas(
        String id, 
        String nombre, 
        String insigniaID, 
        CategoriaDonadorEnum inicio, 
        CategoriaDonadorEnum fin){
            super(id, nombre, insigniaID, inicio, fin, TipoMisionEnum.DONACIONES_EXITOSAS);
        }

    @Override
    public boolean estaCompleta(List<DonacionSimulada> donaciones){
        return donaciones.stream()
                    .filter(d -> d.fueAceptada())
                    .count() >= 20;
    }

}
