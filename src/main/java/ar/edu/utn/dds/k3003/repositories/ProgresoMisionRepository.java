package ar.edu.utn.dds.k3003.repositories;

import java.util.List;
import java.util.Optional;

import ar.edu.utn.dds.k3003.model.ProgresoMision;

public interface ProgresoMisionRepository {

    ProgresoMision save(ProgresoMision progreso);

    Optional<ProgresoMision> findByDonadorIdAndMisionId(
            String donadorId,
            String misionId
    );

    List<ProgresoMision> findByDonadorId(String donadorId);

    void deleteAll();
}