package ar.edu.utn.dds.k3003.app;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ar.edu.utn.dds.k3003.repositories.DonadorIncentivoRepository;
import ar.edu.utn.dds.k3003.Fachada;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ProcesamientoCron {
    private final DonadorIncentivoRepository donadorRepository;
    private final Fachada fachada;
    private final MeterRegistry meterRegistry;

    public ProcesamientoCron(
            DonadorIncentivoRepository donadorRepository,
            Fachada fachada,
            MeterRegistry meterRegistry) {
        this.donadorRepository = donadorRepository;
        this.fachada = fachada;
        this.meterRegistry = meterRegistry;
        }
    
    @Scheduled(fixedRate = 60000)
    public void ejecutarProcesamiento() {
        
          Counter.builder("donatrack.incentivos.cron.ejecuciones")
                .register(meterRegistry)
                .increment();
        
        List<String> donadorIds = donadorRepository.findAllIds();

        for (String donadorId : donadorIds) {
            try {
                
                fachada.procesarDonador(donadorId);
            } catch (Exception e) {
                // Si falla un donador, el catch lo frena acá y continúa con los siguientes
                System.err.println("Error procesando misiones del donador ID " + donadorId + ": " + e.getMessage());
            }
        }
    }
}