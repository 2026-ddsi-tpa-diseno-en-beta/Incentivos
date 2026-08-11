package ar.edu.utn.dds.k3003.app;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ar.edu.utn.dds.k3003.repositories.DonadorIncentivoRepository;
import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.model.DonadorIncentivo;
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
    public void procesarDonadores() {
        
          Counter.builder("donatrack.incentivos.cron.ejecuciones")
                .register(meterRegistry)
                .increment();

        for (DonadorIncentivo donador : donadorRepository.findAll()) {

            if (donador.getMisionActual() != null) {
                try {
                    fachada.procesarDonador(donador.getDonadorId());
                } catch (Exception e) {
                    System.err.println(
                        "Error procesando donador "
                        + donador.getDonadorId()
                        + ": "
                        + e.getMessage()
                    );
                }
            }
        }
    }
}