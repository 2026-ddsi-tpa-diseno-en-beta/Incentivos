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
    
    @Scheduled(fixedDelayString = "${incentivos.cron.intervalo-ms:60000}", initialDelayString = "${incentivos.cron.inicio-ms:60000}")
    public void ejecutarProcesamiento() {
        
          Counter.builder("donatrack.incentivos.cron.ejecuciones")
                .register(meterRegistry)
                .increment();
        
        List<String> donadorIds = donadorRepository.findAllIds();

        for (String donadorId : donadorIds) {
            var previous = org.slf4j.MDC.getCopyOfContextMap();
            org.slf4j.MDC.put("traceId", java.util.UUID.randomUUID().toString());
            org.slf4j.MDC.put("component", "incentivos");
            org.slf4j.MDC.put("instanceId", System.getenv().getOrDefault("INSTANCE_ID", "local"));
            try {
                
                fachada.procesarDonador(donadorId);
            } catch (Exception e) {
                // Si falla un donador, el catch lo frena acá y continúa con los siguientes
                Counter.builder("donatrack.incentivos.cron.errores").register(meterRegistry).increment();
                org.slf4j.LoggerFactory.getLogger(ProcesamientoCron.class).error("cron.donador_error donador={}", donadorId, e);
            } finally {
                org.slf4j.MDC.clear();
                if (previous != null) org.slf4j.MDC.setContextMap(previous);
            }
        }
    }
}
