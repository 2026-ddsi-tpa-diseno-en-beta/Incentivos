package ar.edu.utn.dds.k3003.model;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;

public class MisionCompletitud extends Mision{
    
    public MisionCompletitud(
        String id, 
        String nombre, 
        String insigniaID, 
        CategoriaDonadorEnum inicio, 
        CategoriaDonadorEnum fin){
            super(id, nombre, insigniaID, inicio, fin, TipoMisionEnum.COMPLETITUD);
        }
    
    @Override
    public boolean estaCompleta(List<DonacionSimulada> donaciones){
        return donaciones.stream()
                    .map(d -> d.getCategoria())
                    .distinct()
                    .count() >= 3;
    }
}
