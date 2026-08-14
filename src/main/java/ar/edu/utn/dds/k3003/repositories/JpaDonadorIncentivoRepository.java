package ar.edu.utn.dds.k3003.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.DonadorIncentivo;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataDonadoresRepository;

@Repository
public class JpaDonadorIncentivoRepository implements DonadorIncentivoRepository{
    
    private final SpringDataDonadoresRepository repository;

    public JpaDonadorIncentivoRepository(SpringDataDonadoresRepository repository) {
    this.repository = repository;
  }
  @Override
  public DonadorIncentivo save(DonadorIncentivo donador){
    return repository.save(donador);
  }
    
  @Override
  public  Optional<DonadorIncentivo> findById(String id){
    return repository.findById(id);
  }

  @Override
  public  List<DonadorIncentivo> findAll(){
    return repository.findAll();
  }

  @Override
    public List<String> findAllIds() { 
        return repository.findAllIds();
  }

  @Override
  public void deleteAll(){
    repository.deleteAll();
  }
}
