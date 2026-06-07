package ar.edu.utn.dds.k3003.model;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;

public class MisionRevolucionDonadora extends Mision{
    public MisionRevolucionDonadora(
            String id,
            String nombre,
            String insigniaID,
            CategoriaDonadorEnum categoriaInicio,
            CategoriaDonadorEnum categoriaFin) {

        super(id, nombre, insigniaID, categoriaInicio, categoriaFin,
                TipoMisionEnum.REVOLUCION_DONADORA);
        }
    @Override
    public boolean estaCompleta(List<DonacionSimulada> donaciones){
        return donaciones.stream()
                .filter(d -> d.getCantidad() > 50)
                .count() > 10;
    }
}
