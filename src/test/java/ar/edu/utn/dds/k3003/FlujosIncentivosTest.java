package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.*;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.*;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FlujosIncentivosTest {
  DonadorIncentivoRepository donadores;
  MisionRepository misiones;
  InsigniaRepository insignias;
  ProgresoMisionRepository progresos;
  FachadaDonadoresYEntidades externo;
  FachadaDonaciones donaciones;
  SimpleMeterRegistry registry;
  Fachada fachada;
  DonadorIncentivo donador;
  Mision mision;
  ProgresoMision progreso;
  @BeforeEach void setup() {
    donadores=mock(DonadorIncentivoRepository.class); misiones=mock(MisionRepository.class);
    insignias=mock(InsigniaRepository.class); progresos=mock(ProgresoMisionRepository.class);
    externo=mock(FachadaDonadoresYEntidades.class); donaciones=mock(FachadaDonaciones.class);
    registry=new SimpleMeterRegistry();
    fachada=new Fachada(donadores,insignias,misiones,registry,progresos);
    fachada.setFachadaDonadoresYEntidades(externo); fachada.setFachadaDonaciones(donaciones);
    donador=new DonadorIncentivo("d"); donador.avanzarCategoria(CategoriaDonadorEnum.COLABORADOR);
    mision=new MisionDonacionesExitosas("m","Exitosas","b",CategoriaDonadorEnum.COLABORADOR,CategoriaDonadorEnum.TRANSFORMADOR);
    donador.asignarMision(mision); progreso=new ProgresoMision("d","m");
    when(donadores.findById("d")).thenReturn(Optional.of(donador));
    when(misiones.findById("m")).thenReturn(Optional.of(mision));
    when(progresos.findByDonadorId("d")).thenReturn(List.of(progreso));
    when(progresos.findByDonadorIdAndMisionId("d","m")).thenReturn(Optional.of(progreso));
    when(insignias.findById("b")).thenReturn(Optional.of(new Insignia("b","Premio","")));
    when(donaciones.buscarProductoPorID("p")).thenReturn(new ProductoDTO("p","Producto","Descripción válida","c",null));
  }
  void donations(int count) {
    when(donaciones.buscarPorDonadorYFechaInicio(eq("d"),any())).thenReturn(IntStream.range(0,count)
        .mapToObj(n -> new DonacionDTO("x"+n,"d","dep","Donación","p",1,EstadoDonacionEnum.ACEPTADA)).toList());
  }
  @Test void completarActualizaCategoriaEnLosDosComponentes() {
    donations(20); fachada.procesarDonador("d");
    assertTrue(progreso.estaCompletada()); assertEquals(CategoriaDonadorEnum.TRANSFORMADOR,donador.getCategoria());
    assertEquals(1,donador.getInsignias().size()); verify(externo).modifcarCategoria("d","TRANSFORMADOR");
  }
  @Test void quejaRevocaInsigniaCategoriaYProgreso() {
    donations(20); fachada.procesarDonador("d"); donations(19); fachada.procesarDonador("d");
    assertFalse(progreso.estaCompletada()); assertTrue(donador.getInsignias().isEmpty());
    assertEquals(CategoriaDonadorEnum.COLABORADOR,donador.getCategoria()); verify(externo).modifcarCategoria("d","COLABORADOR");
    assertEquals(1,registry.get("donatrack.incentivos.misiones.revocadas").counter().count());
  }
  @Test void repetirProcesamientoNoDuplicaPremios() {
    donations(20); fachada.procesarDonador("d"); fachada.procesarDonador("d");
    assertEquals(1,donador.getInsignias().size()); verify(externo,times(1)).modifcarCategoria("d","TRANSFORMADOR");
  }
  @Test void procesarSinMisionesCuentaAlDonadorUnaSolaVez() {
    donations(0); when(progresos.findByDonadorId("d")).thenReturn(List.of()); fachada.procesarDonador("d");
    assertEquals(1,registry.get("donatrack.incentivos.donadores.procesados").counter().count());
  }
  @Test void noPermiteDosMisionesActivas() {
    var dto=new MisionDTO("m","Exitosas","b",CategoriaDonadorEnum.COLABORADOR,CategoriaDonadorEnum.TRANSFORMADOR,TipoMisionEnum.DONACIONES_EXITOSAS);
    assertThrows(IllegalArgumentException.class,()->fachada.asignarMisionADonador("d",dto));
  }
  @Test void asignarInsigniaManualEsIdempotente() {
    fachada.asignarInsigniaADonador("d",new InsigniaDTO("b","Premio",""));
    fachada.asignarInsigniaADonador("d",new InsigniaDTO("b","Premio",""));
    assertEquals(1,donador.getInsignias().size());
  }
  @Test void identificadoresNoSeReutilizanAlReiniciarLaFachada() {
    var a=fachada.agregarInsignia(new InsigniaDTO(null,"A","Descripción"));
    var otra=new Fachada(donadores,insignias,misiones,registry,progresos);
    var b=otra.agregarInsignia(new InsigniaDTO(null,"B","Descripción"));
    assertNotEquals(a.id(),b.id()); assertDoesNotThrow(()->UUID.fromString(a.id()));
  }
  @Test void noPermiteBorrarUnaRecompensaReferenciada() {
    when(misiones.findAll()).thenReturn(List.of(mision));
    assertThrows(IllegalArgumentException.class,()->fachada.eliminarInsignia("b")); verify(insignias,never()).deleteById(any());
  }
}
