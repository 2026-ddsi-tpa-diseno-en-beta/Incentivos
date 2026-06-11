package ar.edu.utn.dds.k3003.repositories;
import java.util.List;
import java.util.Optional;

import ar.edu.utn.dds.k3003.model.DonadorIncentivo;


public interface DonadorIncentivoRepository {
    DonadorIncentivo save(DonadorIncentivo donador);
    Optional<DonadorIncentivo> findById(String id);
    List<DonadorIncentivo> findAll();
    void deleteAll();
}