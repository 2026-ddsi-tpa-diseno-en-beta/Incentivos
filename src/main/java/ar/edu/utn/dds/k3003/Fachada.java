package ar.edu.utn.dds.k3003;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.model.DonacionSimulada;
import ar.edu.utn.dds.k3003.model.DonadorIncentivo;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.model.MisionFactory;
import ar.edu.utn.dds.k3003.repositories.IncentivosMapper;
import ar.edu.utn.dds.k3003.repositories.DonadorIncentivoRepository;
import ar.edu.utn.dds.k3003.repositories.InsigniaRepository;
import ar.edu.utn.dds.k3003.repositories.MisionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Fachada implements FachadaIncentivos {
    private FachadaDonaciones fachadaDonaciones;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    private final DonadorIncentivoRepository donadorRepository;
    private final InsigniaRepository insigniaRepository;
    private final MisionRepository misionRepository;
    

   private int contadorIds=1;

    private String generarId(){
      return String.valueOf(contadorIds++);
    }

    

  @Autowired
  public Fachada(DonadorIncentivoRepository donadorRepository,
        InsigniaRepository insigniaRepository,
        MisionRepository misionRepository) {
    /*
    Para que se ejecuten correctamente los tests, se necesita tener un constructor vacio
    Es decir, que no reciba parametros.
    Si necesitan un constructor con parametros
    Java permite tener varios constructores conviviendo sin conflictos.
    */
    this.donadorRepository = donadorRepository;
    this.insigniaRepository = insigniaRepository;
    this.misionRepository = misionRepository;
    
  }

   @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachada) {
        this.fachadaDonadoresYEntidades = fachada;
    }

    @Override
    public void setFachadaDonaciones(FachadaDonaciones f) { 
      this.fachadaDonaciones = f; 
    }

    private void validarQueDonadorExiste(String donadorID) {
      if(donadorID==null) throw new RuntimeException("El donador no existe en el sistema");
        fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    }

    private DonadorIncentivo obtenerODarDeAltaDonador(String donadorID) {
        DonadorIncentivo donador = donadorRepository.findById(donadorID).orElse(null);

        if (donador == null) {
            donador = new DonadorIncentivo(donadorID);
            donadorRepository.save(donador);
        }

        return donador;
    }
    


  @Override
  public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO){
    if(insigniaDTO==null){
      throw new RuntimeException("La insignia no existe");
    }

    if(insigniaDTO.id() != null && insigniaRepository.findById(insigniaDTO.id()).orElse(null) != null){
      throw new RuntimeException("La insignia ya existe");
    }
    String id = insigniaDTO.id() != null ? insigniaDTO.id() : generarId();
    Insignia insignia = new Insignia(id, insigniaDTO.nombre(), insigniaDTO.descripcion());
    insigniaRepository.save(insignia); 
    
    return IncentivosMapper.toInsigniaDTO(insignia);
  }

  @Override 
  public MisionDTO agregarMision(MisionDTO misionDTO){
    if(misionDTO == null){
      throw new RuntimeException("La mision no existe");
    }

    if(misionDTO.id() != null && misionRepository.findById(misionDTO.id()).orElse(null) != null){
      throw new RuntimeException("La mision ya existe");
    }

    String id= generarId();

    Mision mision = MisionFactory.crear(id, misionDTO); 
    
    misionRepository.save(mision);
    
    return IncentivosMapper.toMisionDTO(mision);
  }

  @Override
  public void asignarInsigniaADonador(String donadorID, InsigniaDTO dto){
    this.validarQueDonadorExiste(donadorID);
    if(dto == null || dto.id() == null){
        throw new RuntimeException("InsigniaDTO invalida");
    }

    DonadorIncentivo donador = obtenerODarDeAltaDonador(donadorID);

    Insignia insignia = insigniaRepository.findById(dto.id()).orElse(null);
    if(insignia == null){
        throw new RuntimeException("La insignia no existe");
    }

    donador.agregarInsignia(insignia);
    donadorRepository.save(donador);
    
  }

  @Override
  public void asignarMisionADonador(String donadorID, MisionDTO misionDTO){
    this.validarQueDonadorExiste(donadorID);
    
    if(misionDTO == null || misionDTO.id() == null){
        throw new RuntimeException("MisionDTO invalida");
    }

    DonadorIncentivo donador= obtenerODarDeAltaDonador(donadorID);

    Mision mision = misionRepository.findById(misionDTO.id()).orElse(null);
    if(mision == null){
      throw new RuntimeException("la misión no existe");
    }

    donador.asignarMision(mision);
    donadorRepository.save(donador);
    
  }

  @Override
  public List<InsigniaDTO> getInsigniasDeDonador(String donadorID){
    DonadorIncentivo donador= donadorRepository.findById(donadorID).orElse(null);

    if(donador==null){
      throw new RuntimeException("Donador no encontrado en el sistema");
    }
    return donador.getInsignias().stream()
                  .map(insignia -> IncentivosMapper.toInsigniaDTO(insignia))
                  .toList();
  }

  @Override
  public MisionDTO getMisionEnCursoDeDonador(String donadorID){

    DonadorIncentivo donador = donadorRepository.findById(donadorID).orElse(null);

    if(donador == null){
        throw new RuntimeException("Donador no encontrado en el sistema");
    }
    Mision mision = donador.getMisionActual();

    if(mision == null){return null;};

    return IncentivosMapper.toMisionDTO(mision);
}

  @Override
  public void procesarDonador(String donadorID){
    this.validarQueDonadorExiste(donadorID);

    DonadorIncentivo donador = obtenerODarDeAltaDonador(donadorID);

    var donacionesDTO= fachadaDonaciones.buscarPorDonadorYFechaInicio(
      donadorID, LocalDate.of(2025, 1, 1)
    );
    List<DonacionSimulada> donaciones = donacionesDTO.stream()
                  .map(d -> {

                    ProductoDTO producto =
                            fachadaDonaciones.buscarProductoPorID(d.productoID());

                    return new DonacionSimulada(
                            producto.categoriaID(),
                            d.cantidad(),
                            d.estado() == EstadoDonacionEnum.ACEPTADA
                    );
                })
                .toList(); 
    
    Mision mision = donador.getMisionActual();
    if(mision ==null) return;

    if(mision.estaCompleta(donaciones)){
     

      Insignia insignia = insigniaRepository.findById(mision.getInsigniaID()).orElse(null);
        
      if(insignia != null){
      donador.agregarInsignia(insignia);
       
        }
      donador.avanzarCategoria(mision.getCategoriaFin());
      donador.completarMisionActual();
    }
    donadorRepository.save(donador);
  
  }

   public List<InsigniaDTO> getInsignias() {
        return insigniaRepository.findAll()
            .stream()
            .map(IncentivosMapper::toInsigniaDTO)
            .toList();
    }
    public InsigniaDTO buscarInsigniaPorID(String id) {

         Insignia insignia = insigniaRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Insignia inexistente"));

        return IncentivosMapper.toInsigniaDTO(insignia);
    }

    public List<MisionDTO> getMisiones() {
        return misionRepository.findAll()
            .stream()
            .map(IncentivosMapper::toMisionDTO)
            .toList();
    }

    public MisionDTO buscarMisionPorID(String id) {

        Mision mision = misionRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Misión inexistente"));

        return IncentivosMapper.toMisionDTO(mision);
    }

}