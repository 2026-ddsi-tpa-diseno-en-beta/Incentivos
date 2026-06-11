package ar.edu.utn.dds.k3003.repositories.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.dds.k3003.model.DonadorIncentivo;

public interface SpringDataDonadoresRepository extends JpaRepository<DonadorIncentivo,String>{
    
}
