package ar.edu.utn.dds.k3003;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.*;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.*;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;

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

  @BeforeEach
  void setup() {
    donadores = mock(DonadorIncentivoRepository.class);
    misiones = mock(MisionRepository.class);
    insignias = mock(InsigniaRepository.class);
    progresos = mock(ProgresoMisionRepository.class);
    externo = mock(FachadaDonadoresYEntidades.class);
    donaciones = mock(FachadaDonaciones.class);
    registry = new SimpleMeterRegistry();
    fachada = new Fachada(donadores, insignias, misiones, registry, progresos);
    fachada.setFachadaDonadoresYEntidades(externo);
    fachada.setFachadaDonaciones(donaciones);
    donador = new DonadorIncentivo("d");
    donador.avanzarCategoria(CategoriaDonadorEnum.COLABORADOR);
    mision =
        new MisionDonacionesExitosas(
            "m",
            "Exitosas",
            "b",
            CategoriaDonadorEnum.COLABORADOR,
            CategoriaDonadorEnum.TRANSFORMADOR);
    donador.asignarMision(mision);
    progreso = new ProgresoMision("d", "m");
    when(donadores.findById("d")).thenReturn(Optional.of(donador));
    when(misiones.findById("m")).thenReturn(Optional.of(mision));
    when(progresos.findByDonadorId("d")).thenReturn(List.of(progreso));
    when(progresos.findByDonadorIdAndMisionId("d", "m")).thenReturn(Optional.of(progreso));
    when(insignias.findById("b")).thenReturn(Optional.of(new Insignia("b", "Premio", "")));
    when(donaciones.buscarProductoPorID("p"))
        .thenReturn(new ProductoDTO("p", "Producto", "Descripción válida", "c", null));
  }

  void donations(int count) {
    when(donaciones.buscarPorDonadorYFechaInicio(eq("d"), any()))
        .thenReturn(
            IntStream.range(0, count)
                .mapToObj(
                    n ->
                        new DonacionDTO(
                            "x" + n, "d", "dep", "Donación", "p", 1, EstadoDonacionEnum.ACEPTADA))
                .toList());
  }

  @Test
  void completarActualizaCategoriaEnLosDosComponentes() {
    donations(20);
    fachada.procesarDonador("d");
    assertTrue(progreso.estaCompletada());
    assertEquals(CategoriaDonadorEnum.TRANSFORMADOR, donador.getCategoria());
    assertEquals(1, donador.getInsignias().size());
    verify(externo).modifcarCategoria("d", "TRANSFORMADOR");
  }

  @Test
  void quejaRevocaInsigniaCategoriaYProgreso() {
    donations(20);
    fachada.procesarDonador("d");
    donations(19);
    fachada.procesarDonador("d");
    assertFalse(progreso.estaCompletada());
    assertTrue(donador.getInsignias().isEmpty());
    assertEquals(CategoriaDonadorEnum.COLABORADOR, donador.getCategoria());
    verify(externo).modifcarCategoria("d", "COLABORADOR");
    assertEquals(1, registry.get("donatrack.incentivos.misiones.revocadas").counter().count());
  }

  @Test
  void ascendingMissionUsesTheLastFiveQuantitiesAndAdvancesToSalvador() {
    mision =
        new MisionDonacionesAscendentes(
            "m",
            "Ascendentes",
            "b",
            CategoriaDonadorEnum.COLABORADOR,
            CategoriaDonadorEnum.SALVADOR);
    when(misiones.findById("m")).thenReturn(Optional.of(mision));
    when(donaciones.buscarPorDonadorYFechaInicio(eq("d"), any()))
        .thenReturn(
            IntStream.rangeClosed(1, 5)
                .mapToObj(
                    n ->
                        new DonacionDTO(
                            "x" + n, "d", "dep", "Donación", "p", n, EstadoDonacionEnum.ACEPTADA))
                .toList());
    fachada.procesarDonador("d");
    assertTrue(progreso.estaCompletada());
    assertEquals(CategoriaDonadorEnum.SALVADOR, donador.getCategoria());
    verify(externo).modifcarCategoria("d", "SALVADOR");
  }

  @Test
  void revolutionNeedsMoreThanTenDonationsWithMoreThanFiftyUnits() {
    donador.avanzarCategoria(CategoriaDonadorEnum.TRANSFORMADOR);
    mision =
        new MisionRevolucionDonadora(
            "m",
            "Revolución",
            "b",
            CategoriaDonadorEnum.TRANSFORMADOR,
            CategoriaDonadorEnum.REVOLUCIONARIO);
    when(misiones.findById("m")).thenReturn(Optional.of(mision));
    for (int count : List.of(10, 11)) {
      when(donaciones.buscarPorDonadorYFechaInicio(eq("d"), any()))
          .thenReturn(
              IntStream.range(0, count)
                  .mapToObj(
                      n ->
                          new DonacionDTO(
                              "x" + n,
                              "d",
                              "dep",
                              "Donación",
                              "p",
                              51,
                              EstadoDonacionEnum.ACEPTADA))
                  .toList());
      fachada.procesarDonador("d");
      assertEquals(count == 11, progreso.estaCompletada());
    }
    assertEquals(CategoriaDonadorEnum.REVOLUCIONARIO, donador.getCategoria());
  }

  @Test
  void revocationAlsoRemovesDependentRewardAndCannotRegrantItOutOfSequence() {
    when(donaciones.buscarPorDonadorYFechaInicio(eq("d"), any()))
        .thenReturn(
            IntStream.range(0, 20)
                .mapToObj(
                    n ->
                        new DonacionDTO(
                            "x" + n, "d", "dep", "Donación", "p", 60, EstadoDonacionEnum.ACEPTADA))
                .toList());
    fachada.procesarDonador("d");
    var revolution =
        new MisionRevolucionDonadora(
            "r",
            "Revolución",
            "rb",
            CategoriaDonadorEnum.TRANSFORMADOR,
            CategoriaDonadorEnum.REVOLUCIONARIO);
    var revolutionProgress = new ProgresoMision("d", "r");
    donador.asignarMision(revolution);
    when(misiones.findById("r")).thenReturn(Optional.of(revolution));
    when(insignias.findById("rb")).thenReturn(Optional.of(new Insignia("rb", "Revolución", "")));
    when(progresos.findByDonadorId("d")).thenReturn(List.of(progreso, revolutionProgress));
    fachada.procesarDonador("d");
    assertEquals(CategoriaDonadorEnum.REVOLUCIONARIO, donador.getCategoria());
    // A repository may return the dependent mission before its prerequisite.
    when(progresos.findByDonadorId("d")).thenReturn(List.of(revolutionProgress, progreso));
    when(donaciones.buscarPorDonadorYFechaInicio(eq("d"), any()))
        .thenReturn(
            IntStream.range(0, 20)
                .mapToObj(
                    n ->
                        new DonacionDTO(
                            "x" + n,
                            "d",
                            "dep",
                            "Donación",
                            "p",
                            60,
                            n == 0 ? EstadoDonacionEnum.CONQUEJA : EstadoDonacionEnum.ACEPTADA))
                .toList());
    fachada.procesarDonador("d");
    fachada.procesarDonador("d");
    assertEquals(CategoriaDonadorEnum.COLABORADOR, donador.getCategoria());
    assertFalse(revolutionProgress.estaCompletada());
    assertTrue(donador.getInsignias().isEmpty());
    donations(20);
    fachada.procesarDonador("d");
    assertEquals(CategoriaDonadorEnum.TRANSFORMADOR, donador.getCategoria());
    assertFalse(revolutionProgress.estaCompletada());
  }

  @Test
  void repetirProcesamientoNoDuplicaPremios() {
    donations(20);
    fachada.procesarDonador("d");
    fachada.procesarDonador("d");
    assertEquals(1, donador.getInsignias().size());
    verify(externo, times(1)).modifcarCategoria("d", "TRANSFORMADOR");
  }

  @Test
  void procesarSinMisionesCuentaAlDonadorUnaSolaVez() {
    donations(0);
    when(progresos.findByDonadorId("d")).thenReturn(List.of());
    fachada.procesarDonador("d");
    assertEquals(1, registry.get("donatrack.incentivos.donadores.procesados").counter().count());
  }

  @Test
  void noPermiteDosMisionesActivas() {
    var dto =
        new MisionDTO(
            "m",
            "Exitosas",
            "b",
            CategoriaDonadorEnum.COLABORADOR,
            CategoriaDonadorEnum.TRANSFORMADOR,
            TipoMisionEnum.DONACIONES_EXITOSAS);
    assertThrows(IllegalArgumentException.class, () -> fachada.asignarMisionADonador("d", dto));
  }

  @Test
  void asignarInsigniaManualEsIdempotente() {
    fachada.asignarInsigniaADonador("d", new InsigniaDTO("b", "Premio", ""));
    fachada.asignarInsigniaADonador("d", new InsigniaDTO("b", "Premio", ""));
    assertEquals(1, donador.getInsignias().size());
  }

  @Test
  void identificadoresNoSeReutilizanAlReiniciarLaFachada() {
    var a = fachada.agregarInsignia(new InsigniaDTO(null, "A", "Descripción"));
    var otra = new Fachada(donadores, insignias, misiones, registry, progresos);
    var b = otra.agregarInsignia(new InsigniaDTO(null, "B", "Descripción"));
    assertNotEquals(a.id(), b.id());
    assertDoesNotThrow(() -> UUID.fromString(a.id()));
  }

  @Test
  void noPermiteBorrarUnaRecompensaReferenciada() {
    when(misiones.findAll()).thenReturn(List.of(mision));
    assertThrows(IllegalArgumentException.class, () -> fachada.eliminarInsignia("b"));
    verify(insignias, never()).deleteById(any());
  }

  @Test
  void catalogoRechazaMisionesSinRecompensaExistente() {
    var dto =
        new MisionDTO(
            null,
            "Completitud",
            "inexistente",
            CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR,
            TipoMisionEnum.COMPLETITUD);
    assertThrows(NoSuchElementException.class, () -> fachada.agregarMision(dto));
    verify(misiones, never()).save(any());
  }

  @Test
  void catalogoRechazaNombreVacioYReportaAusentes() {
    assertThrows(
        IllegalArgumentException.class,
        () -> fachada.agregarInsignia(new InsigniaDTO(null, " ", "Descripción")));
    assertThrows(NoSuchElementException.class, () -> fachada.buscarInsigniaPorID("inexistente"));
    assertThrows(NoSuchElementException.class, () -> fachada.buscarMisionPorID("inexistente"));
  }

  @Test
  void cannotDeleteAssignedMissionOrItsGrantedBadge() {
    when(donadores.findAll()).thenReturn(List.of(donador));
    assertThrows(IllegalArgumentException.class, () -> fachada.eliminarMision("m"));
    donador.agregarInsignia(new Insignia("b", "Premio", "Demo"));
    when(misiones.findAll()).thenReturn(List.of());
    assertThrows(IllegalArgumentException.class, () -> fachada.eliminarInsignia("b"));
    verify(misiones, never()).deleteById(anyString());
    verify(insignias, never()).deleteById(anyString());
  }

  @Test
  void missingRewardsAndMissionReportAbsenceWithoutCallingDonadores() {
    assertThrows(NoSuchElementException.class, () -> fachada.getInsigniasDeDonador("nuevo"));
    assertThrows(NoSuchElementException.class, () -> fachada.getMisionEnCursoDeDonador("nuevo"));
    assertThrows(NoSuchElementException.class, () -> fachada.getInsigniasDeDonador("d"));
    progreso.completar();
    assertThrows(NoSuchElementException.class, () -> fachada.getMisionEnCursoDeDonador("d"));
    verifyNoInteractions(externo);
  }

  @Test
  void catalogRejectsCategoryJumpsThatDoNotBelongToTheMission() {
    for (TipoMisionEnum type : TipoMisionEnum.values()) {
      var dto =
          new MisionDTO(
              null,
              "Inválida",
              "b",
              CategoriaDonadorEnum.OCASIONAL,
              CategoriaDonadorEnum.REVOLUCIONARIO,
              type);
      assertThrows(IllegalArgumentException.class, () -> fachada.agregarMision(dto));
    }
    verify(misiones, never()).save(any());
  }

  @Test
  void rejectsMissingAndRepeatedReferencesBeforeChangingAssignments() {
    assertThrows(IllegalArgumentException.class, () -> fachada.procesarDonador(null));
    assertThrows(IllegalArgumentException.class, () -> fachada.agregarInsignia(null));
    assertThrows(IllegalArgumentException.class, () -> fachada.agregarMision(null));
    assertThrows(IllegalArgumentException.class, () -> fachada.asignarInsigniaADonador("d", null));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.agregarMision(
                new MisionDTO(
                    "m",
                    "Duplicada",
                    "b",
                    CategoriaDonadorEnum.COLABORADOR,
                    CategoriaDonadorEnum.TRANSFORMADOR,
                    TipoMisionEnum.DONACIONES_EXITOSAS)));
    assertThrows(
        NoSuchElementException.class,
        () ->
            fachada.asignarMisionADonador(
                "d",
                new MisionDTO(
                    "missing",
                    "Ausente",
                    "b",
                    CategoriaDonadorEnum.COLABORADOR,
                    CategoriaDonadorEnum.TRANSFORMADOR,
                    TipoMisionEnum.DONACIONES_EXITOSAS)));
    assertThrows(IllegalArgumentException.class, () -> fachada.asignarMisionADonador("d", null));
    assertThrows(
        NoSuchElementException.class,
        () -> fachada.asignarInsigniaADonador("d", new InsigniaDTO("missing", "", "")));
    assertThrows(
        IllegalArgumentException.class,
        () -> fachada.agregarInsignia(new InsigniaDTO("b", "Duplicado", "Demo")));
    when(progresos.existsByDonadorIdAndMisionId("d", "m")).thenReturn(true);
    assertThrows(
        RuntimeException.class,
        () ->
            fachada.asignarMisionADonador(
                "d",
                new MisionDTO(
                    "m",
                    "Exitosas",
                    "b",
                    CategoriaDonadorEnum.COLABORADOR,
                    CategoriaDonadorEnum.TRANSFORMADOR,
                    TipoMisionEnum.DONACIONES_EXITOSAS)));
  }
}
