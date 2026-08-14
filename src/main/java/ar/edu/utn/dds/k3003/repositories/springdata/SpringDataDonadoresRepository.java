package ar.edu.utn.dds.k3003.repositories.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.edu.utn.dds.k3003.model.DonadorIncentivo;

public interface SpringDataDonadoresRepository extends JpaRepository<DonadorIncentivo,String>{
    @Query("SELECT d.donadorId FROM DonadorIncentivo d")
    List<String> findAllIds();
}
