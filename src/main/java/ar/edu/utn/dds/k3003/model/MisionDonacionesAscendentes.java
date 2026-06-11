package ar.edu.utn.dds.k3003.model;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DONACIONES_ASCENDENTES")
public class MisionDonacionesAscendentes extends Mision{
     public MisionDonacionesAscendentes() {
    super();
}
    
    public MisionDonacionesAscendentes(
        String id, 
        String nombre, 
        String insigniaID, 
        CategoriaDonadorEnum inicio, 
        CategoriaDonadorEnum fin){
            super(id, nombre, insigniaID, inicio, fin, TipoMisionEnum.DONACIONES_ASCENDENTES);
        }
    
    @Override 
    public boolean estaCompleta(List<DonacionSimulada> donaciones){
        if(donaciones.size() < 5){
            return false;
        }
        List<DonacionSimulada> ultimasDonaciones = donaciones.subList(donaciones.size() - 5, donaciones.size());

        return esAscendente(ultimasDonaciones);
    }

    private boolean esAscendente(List<DonacionSimulada> donaciones){
        for (int i = 1; i < donaciones.size(); i++) {

        if (donaciones.get(i).getCantidad()
                <= donaciones.get(i - 1).getCantidad()) {
            return false;
        }
    }

    return true;
    }
}