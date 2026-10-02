package ar.edu.utn.dds.k3003.app;
import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.repositories.DonadorIncentivoRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProcesamientoCronTest {
  @Test void failedDonorDoesNotStopNextOneAndDoesNotAdvertiseCompleteSuccess() {
    var repository = mock(DonadorIncentivoRepository.class);
    var fachada = mock(Fachada.class);
    var registry = new SimpleMeterRegistry();
    when(repository.findAllIds()).thenReturn(List.of("a", "b"));
    doThrow(new IllegalStateException("falla")).when(fachada).procesarDonador("a");
    var cron = new ProcesamientoCron(repository, fachada, registry);
    cron.ejecutarProcesamiento();
    verify(fachada).procesarDonador("b");
    assertEquals(0, registry.get("donatrack.incentivos.cron.ultima.ejecucion.exitosa").gauge().value());
    assertEquals(1, registry.get("donatrack.incentivos.cron.pendientes").gauge().value());
    assertEquals(1, registry.get("donatrack.incentivos.cron.errores").counter().count());
    assertEquals(1, registry.get("donatrack.incentivos.cron.duracion").timer().count());
    reset(fachada);
    cron.ejecutarProcesamiento();
    assertTrue(registry.get("donatrack.incentivos.cron.ultima.ejecucion.exitosa").gauge().value() > 0);
    assertEquals(0, registry.get("donatrack.incentivos.cron.pendientes").gauge().value());
  }
}
