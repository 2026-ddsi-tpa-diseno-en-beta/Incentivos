package ar.edu.utn.dds.k3003;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.model.DonacionSimulada;
import ar.edu.utn.dds.k3003.model.DonadorIncentivo;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.model.MisionFactory;
import ar.edu.utn.dds.k3003.model.ProgresoMision;
import ar.edu.utn.dds.k3003.repositories.IncentivosMapper;
import ar.edu.utn.dds.k3003.repositories.DonadorIncentivoRepository;
import ar.edu.utn.dds.k3003.repositories.InsigniaRepository;
import ar.edu.utn.dds.k3003.repositories.MisionRepository;
import ar.edu.utn.dds.k3003.repositories.ProgresoMisionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Fachada implements FachadaIncentivos {
    private FachadaDonaciones fachadaDonaciones;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    private final DonadorIncentivoRepository donadorRepository;
    private final InsigniaRepository insigniaRepository;
    private final MisionRepository misionRepository;
    private final MeterRegistry meterRegistry;
    private final ProgresoMisionRepository progresoMisionRepository;

   private int contadorIds=1;

    private String generarId(){
      return String.valueOf(contadorIds++);
    }

  private void incrementarMetrica(String nombre) {
    if (meterRegistry != null) {
        Counter.builder(nombre)
                .register(meterRegistry)
                .increment();
    }
}

  @Autowired
  public Fachada(DonadorIncentivoRepository donadorRepository,
        InsigniaRepository insigniaRepository,
        MisionRepository misionRepository, 
        MeterRegistry meterRegistry,
        ProgresoMisionRepository progresoMisionRepository) {
    /*
    Para que se ejecuten correctamente los tests, se necesita tener un constructor vacio
    Es decir, que no reciba parametros.
    Si necesitan un constructor con parametros
    Java permite tener varios constructores conviviendo sin conflictos.
    */
    this.donadorRepository = donadorRepository;
    this.insigniaRepository = insigniaRepository;
    this.misionRepository = misionRepository;
    this.meterRegistry = meterRegistry;
    this.progresoMisionRepository = progresoMisionRepository;
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
      if(donadorID==null){ 
        incrementarMetrica("donatrack.incentivos.errores");
        throw new RuntimeException("El donador no existe en el sistema");
      }
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

    private ProgresoMision obtenerProgresoActual(DonadorIncentivo donador) {
    for (Mision mision : donador.getMisiones()) {

        ProgresoMision progreso =
                progresoMisionRepository
                        .findByDonadorIdAndMisionId(
                                donador.getDonadorId(),
                                mision.getId()
                        )
                        .orElse(null);

        if (progreso != null && !progreso.estaCompletada()) {
            return progreso;
        }
    }
    return null;
    }

    private List<ProgresoMision> obtenerProgresosDelDonador(
        DonadorIncentivo donador) {

    return progresoMisionRepository
            .findByDonadorId(donador.getDonadorId());
}
    


  @Override
  public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO){
    if(insigniaDTO==null){
      incrementarMetrica("donatrack.incentivos.errores");
      throw new RuntimeException("La insignia no existe");
    }

    if(insigniaDTO.id() != null && insigniaRepository.findById(insigniaDTO.id()).orElse(null) != null){
      incrementarMetrica("donatrack.incentivos.errores");
      throw new RuntimeException("La insignia ya existe");
    }
    String id = insigniaDTO.id() != null ? insigniaDTO.id() : generarId();
    Insignia insignia = new Insignia(id, insigniaDTO.nombre(), insigniaDTO.descripcion());
    insigniaRepository.save(insignia); 
    incrementarMetrica("donatrack.incentivos.insignias.creadas");
    return IncentivosMapper.toInsigniaDTO(insignia);
  }

  @Override 
  public MisionDTO agregarMision(MisionDTO misionDTO){
    if(misionDTO == null){
      incrementarMetrica("donatrack.incentivos.errores");
      throw new RuntimeException("La mision no existe");
    }

    if(misionDTO.id() != null && misionRepository.findById(misionDTO.id()).orElse(null) != null){
      incrementarMetrica("donatrack.incentivos.errores");
      throw new RuntimeException("La mision ya existe");
    }

    String id= generarId();

    Mision mision = MisionFactory.crear(id, misionDTO); 
    
    misionRepository.save(mision);
    incrementarMetrica("donatrack.incentivos.misiones.creadas");
    return IncentivosMapper.toMisionDTO(mision);
  }

  @Override
  public void asignarInsigniaADonador(String donadorID, InsigniaDTO dto){
    this.validarQueDonadorExiste(donadorID);
    if(dto == null || dto.id() == null){
      incrementarMetrica("donatrack.incentivos.errores");
        throw new RuntimeException("InsigniaDTO invalida");
    }

    DonadorIncentivo donador = obtenerODarDeAltaDonador(donadorID);

    Insignia insignia = insigniaRepository.findById(dto.id()).orElse(null);
    if(insignia == null){
      incrementarMetrica("donatrack.incentivos.errores");
        throw new RuntimeException("La insignia no existe");
    }

    donador.agregarInsignia(insignia);
    donadorRepository.save(donador);
    
  }

  @Override
  public void asignarMisionADonador(String donadorID, MisionDTO misionDTO){
    this.validarQueDonadorExiste(donadorID);
    
    if(misionDTO == null || misionDTO.id() == null){
      incrementarMetrica("donatrack.incentivos.errores");
        throw new RuntimeException("MisionDTO invalida");
    }

    DonadorIncentivo donador= obtenerODarDeAltaDonador(donadorID);

    Mision mision = misionRepository.findById(misionDTO.id()).orElse(null);
    if(mision == null){
      incrementarMetrica("donatrack.incentivos.errores");
      throw new RuntimeException("la misión no existe");
    }

    if (progresoMisionRepository.existsByDonadorIdAndMisionId(
        donadorID,
        mision.getId())) {

    incrementarMetrica("donatrack.incentivos.errores");

    throw new RuntimeException(
            "La misión ya está asignada al donador"
    );
}

    donador.asignarMision(mision);

    if (!progresoMisionRepository.existsByDonadorIdAndMisionId(
        donadorID,
        mision.getId())) {

    ProgresoMision progreso =
            new ProgresoMision(
                    donadorID,
                    mision.getId()
            );

    progresoMisionRepository.save(progreso);
  }
    donadorRepository.save(donador);
    incrementarMetrica("donatrack.incentivos.misiones.asignadas");
  }

  @Override
  public List<InsigniaDTO> getInsigniasDeDonador(String donadorID){
    incrementarMetrica("donatrack.incentivos.consultas");

    DonadorIncentivo donador= donadorRepository.findById(donadorID).orElse(null);

    if(donador==null){
      incrementarMetrica("donatrack.incentivos.errores");
      throw new RuntimeException("Donador no encontrado en el sistema");
    }
    return donador.getInsignias().stream()
                  .map(insignia -> IncentivosMapper.toInsigniaDTO(insignia))
                  .toList();
  }

  @Override
  public MisionDTO getMisionEnCursoDeDonador(String donadorID){
    incrementarMetrica("donatrack.incentivos.consultas");

    DonadorIncentivo donador = donadorRepository.findById(donadorID).orElse(null);

    if(donador == null){
      incrementarMetrica("donatrack.incentivos.errores");
        throw new RuntimeException("Donador no encontrado en el sistema");
    }
  ProgresoMision progreso = obtenerProgresoActual(donador);

  if (progreso == null) {
    incrementarMetrica("donatrack.incentivos.errores");
            throw new NoSuchElementException("El donador " + donadorID + " no tiene ninguna misión en curso");
  }

  Mision mision = misionRepository
        .findById(progreso.getMisionId())
        .orElseThrow(() ->
                new RuntimeException("Misión inexistente"));

  return IncentivosMapper.toMisionDTO(mision);
}

  @Override
  @Transactional
  public void procesarDonador(String donadorID) {
    this.validarQueDonadorExiste(donadorID);

    DonadorIncentivo donador = obtenerODarDeAltaDonador(donadorID);

    var donacionesDTO =
            fachadaDonaciones.buscarPorDonadorYFechaInicio(
                    donadorID,
                    LocalDate.of(2025, 1, 1)
            );

    List<DonacionSimulada> donaciones =
            donacionesDTO.stream()
                    .map(d -> {
                        ProductoDTO producto =
                                fachadaDonaciones.buscarProductoPorID(
                                        d.productoID()
                                );

                        return new DonacionSimulada(
                                producto.categoriaID(),
                                d.cantidad(),
                                d.estado() == EstadoDonacionEnum.ACEPTADA
                        );
                    })
                    .toList();

    List<ProgresoMision> progresos =
            obtenerProgresosDelDonador(donador);

    for (ProgresoMision progreso : progresos) {

        Mision mision =
                misionRepository
                        .findById(progreso.getMisionId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Misión inexistente"
                                )
                        );


if (mision.estaCompleta(donaciones)  && !progreso.estaCompletada()) {

    // La misión se completa por primera vez
    

        Insignia insignia =
                insigniaRepository
                        .findById(mision.getInsigniaID())
                        .orElse(null);

        if (insignia != null) {
            donador.agregarInsignia(insignia);
        }

        CategoriaDonadorEnum nuevaCategoria =
                mision.getCategoriaFin();

        if (nuevaCategoria != null) {
            donador.avanzarCategoria(nuevaCategoria);
        }

        progreso.completar();
        progresoMisionRepository.save(progreso);
    

} else if ( !mision.estaCompleta(donaciones) &&
        progreso.estaCompletada()
        && mision.getTipo() == TipoMisionEnum.DONACIONES_EXITOSAS
) {

    // Donaciones Exitosas dejó de cumplirse.
    // Se pierde todo el progreso de la misión.

    progreso.descompletar();

    // Volver a la categoría desde la cual se obtenía la misión
    CategoriaDonadorEnum categoriaAnterior =
            mision.getCategoriaInicio();

    if (categoriaAnterior != null) {
        donador.retrocederCategoria(categoriaAnterior);
    }

    // Quitar la insignia correspondiente a la misión
    if (mision.getInsigniaID() != null) {
        donador.quitarInsignia(
                mision.getInsigniaID()
        );
    }

    progresoMisionRepository.save(progreso);
}

        donadorRepository.save(donador);
        incrementarMetrica("donatrack.incentivos.donadores.procesados");
}
  }

   public List<InsigniaDTO> getInsignias() {
       incrementarMetrica("donatrack.incentivos.consultas");

        return insigniaRepository.findAll()
            .stream()
            .map(IncentivosMapper::toInsigniaDTO)
            .toList();
    }
    public InsigniaDTO buscarInsigniaPorID(String id) {
         incrementarMetrica("donatrack.incentivos.consultas");

         Insignia insignia = insigniaRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Insignia inexistente"));

        return IncentivosMapper.toInsigniaDTO(insignia);
    }

    public List<MisionDTO> getMisiones() {
        incrementarMetrica("donatrack.incentivos.consultas");
        return misionRepository.findAll()
            .stream()
            .map(IncentivosMapper::toMisionDTO)
            .toList();
    }

    public MisionDTO buscarMisionPorID(String id) {
       incrementarMetrica("donatrack.incentivos.consultas");

        Mision mision = misionRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Misión inexistente"));

        return IncentivosMapper.toMisionDTO(mision);
    }

    @Transactional
    public void limpiarDatos() {
    progresoMisionRepository.deleteAll();
    donadorRepository.deleteAll();
    misionRepository.deleteAll();
    insigniaRepository.deleteAll();
}
}