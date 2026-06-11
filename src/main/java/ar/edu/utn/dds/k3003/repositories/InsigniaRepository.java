package ar.edu.utn.dds.k3003.repositories;
import ar.edu.utn.dds.k3003.model.Insignia;

import java.util.List;
import java.util.Optional;


public interface InsigniaRepository {
    Insignia save(Insignia insignia);
    Optional<Insignia> findById(String id);
    List<Insignia> findAll();
    void deleteAll();
}
