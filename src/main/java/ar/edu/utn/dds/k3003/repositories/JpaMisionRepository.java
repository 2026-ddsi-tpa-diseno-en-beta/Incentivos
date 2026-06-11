package ar.edu.utn.dds.k3003.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataMisionesRepository;

@Repository
public class JpaMisionRepository implements MisionRepository{
    
    private final SpringDataMisionesRepository repository;

    public JpaMisionRepository(SpringDataMisionesRepository repository) {
    this.repository = repository;
  }
  @Override
  public Mision save(Mision mision){
    return repository.save(mision);
  }
    
  @Override
  public  Optional<Mision> findById(String id){
    return repository.findById(id);
  }

  @Override
  public  List<Mision> findAll(){
    return repository.findAll();
  }
  @Override
  public void deleteAll(){
    repository.deleteAll();
  }
}
