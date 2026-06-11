package ar.edu.utn.dds.k3003.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.Insignia;

import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataInsigniasRepository;

@Repository
public class JpaInsigniaRepository implements InsigniaRepository{
    
    private final SpringDataInsigniasRepository repository;

    public JpaInsigniaRepository(SpringDataInsigniasRepository repository) {
    this.repository = repository;
  }
  @Override
  public Insignia save(Insignia insignia){
    return repository.save(insignia);
  }
    
  @Override
  public  Optional<Insignia> findById(String id){
    return repository.findById(id);
  }

  @Override
  public  List<Insignia> findAll(){
    return repository.findAll();
  }
  @Override
  public void deleteAll(){
    repository.deleteAll();
  }
}
