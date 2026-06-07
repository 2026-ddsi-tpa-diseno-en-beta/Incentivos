package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;

public class MisionFactory {
    public static Mision crear(String id, MisionDTO dto) {

        TipoMisionEnum tipo = dto.tipo();

        return switch (tipo) {

            case COMPLETITUD ->
                new MisionCompletitud(
                    id,
                    dto.nombre(),
                    dto.insigniaID(),
                    dto.categoriaInicio(),
                    dto.categoriaFin()
                );

            case DONACIONES_EXITOSAS ->
                new MisionDonacionesExitosas(
                    id,
                    dto.nombre(),
                    dto.insigniaID(),
                    dto.categoriaInicio(),
                    dto.categoriaFin()
                );

            case DONACIONES_ASCENDENTES ->
                new MisionDonacionesAscendentes(
                    id,
                    dto.nombre(),
                    dto.insigniaID(),
                    dto.categoriaInicio(),
                    dto.categoriaFin()
                );

            case REVOLUCION_DONADORA ->
                new MisionRevolucionDonadora(
                    id,
                    dto.nombre(),
                    dto.insigniaID(),
                    dto.categoriaInicio(),
                    dto.categoriaFin()
                );

            default ->
                throw new RuntimeException("Tipo de misión inválido");
        };
    } 
}
