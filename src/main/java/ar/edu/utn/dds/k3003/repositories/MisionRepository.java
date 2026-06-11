package ar.edu.utn.dds.k3003.repositories;
import java.util.List;
import java.util.Optional;

import ar.edu.utn.dds.k3003.model.Mision;

public interface MisionRepository {
    Mision save(Mision mision);
    Optional<Mision> findById(String id);
    List<Mision> findAll();
    void deleteAll();
}
