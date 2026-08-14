package ar.edu.utn.dds.k3003.repositories.springdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.dds.k3003.model.ProgresoMision;

public interface SpringDataProgresoMisionRepository
        extends JpaRepository<ProgresoMision, Long> {

    Optional<ProgresoMision> findByDonadorIdAndMisionId(
            String donadorId,
            String misionId
    );

    List<ProgresoMision> findByDonadorId(String donadorId);

    boolean existsByDonadorIdAndMisionId(
        String donadorId,
        String misionId
);
}