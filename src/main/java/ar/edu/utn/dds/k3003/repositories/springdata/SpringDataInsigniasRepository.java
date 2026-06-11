package ar.edu.utn.dds.k3003.repositories.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.dds.k3003.model.Insignia;

public interface SpringDataInsigniasRepository extends JpaRepository<Insignia,String>{
    
}
