package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.Mision;

public class IncentivosMapper {
    public static InsigniaDTO toInsigniaDTO(Insignia insignia){
        return new InsigniaDTO(
            insignia.getId(),
            insignia.getNombre(),
            insignia.getDescripcion()
        );
    }

     public static MisionDTO toMisionDTO(Mision mision) {
        return new MisionDTO(
                mision.getId(),
                mision.getNombre(),
                mision.getInsigniaID(),
                mision.getCategoriaInicio(),
                mision.getCategoriaFin(),
                mision.getTipo()
        );
    }
}
