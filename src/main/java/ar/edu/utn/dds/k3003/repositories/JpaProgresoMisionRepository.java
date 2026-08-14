package ar.edu.utn.dds.k3003.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.ProgresoMision;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataProgresoMisionRepository;

@Repository
public class JpaProgresoMisionRepository
        implements ProgresoMisionRepository {

    private final SpringDataProgresoMisionRepository repository;

    public JpaProgresoMisionRepository(
            SpringDataProgresoMisionRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProgresoMision save(ProgresoMision progreso) {
        return repository.save(progreso);
    }

    @Override
    public Optional<ProgresoMision> findByDonadorIdAndMisionId(
            String donadorId,
            String misionId) {

        return repository.findByDonadorIdAndMisionId(
                donadorId,
                misionId
        );
    }

    @Override
    public List<ProgresoMision> findByDonadorId(String donadorId) {
        return repository.findByDonadorId(donadorId);
    }

    @Override
    public boolean existsByDonadorIdAndMisionId(
        String donadorId,
        String misionId) {

    return repository.existsByDonadorIdAndMisionId(
            donadorId,
            misionId
    );
}

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}