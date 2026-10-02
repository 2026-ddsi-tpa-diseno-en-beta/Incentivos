package ar.edu.utn.dds.k3003.app;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ar.edu.utn.dds.k3003.repositories.DonadorIncentivoRepository;
import ar.edu.utn.dds.k3003.Fachada;
import io.micrometer.core.instrument.*;
import org.slf4j.*;

@Component
public class ProcesamientoCron {
    private static final Logger log = LoggerFactory.getLogger(ProcesamientoCron.class);
    private final DonadorIncentivoRepository donadorRepository;
    private final Fachada fachada;
    private final Counter executions;
    private final Counter errors;
    private final Timer duration;
    private volatile double lastSuccess;
    private volatile double pending;

    public ProcesamientoCron(DonadorIncentivoRepository repository, Fachada fachada, MeterRegistry registry) {
        this.donadorRepository = repository;
        this.fachada = fachada;
        executions = registry.counter("donatrack.incentivos.cron.ejecuciones");
        errors = registry.counter("donatrack.incentivos.cron.errores");
        duration = Timer.builder("donatrack.incentivos.cron.duracion").publishPercentileHistogram().register(registry);
        Gauge.builder("donatrack.incentivos.cron.ultima.ejecucion.exitosa", this, cron -> cron.lastSuccess)
            .baseUnit("seconds").description("Unix timestamp del último ciclo completo sin errores; 0 si nunca terminó").register(registry);
        Gauge.builder("donatrack.incentivos.cron.pendientes", this, cron -> cron.pending)
            .description("Donadores restantes o fallidos del último ciclo; no es una cola persistida").register(registry);
        }
    
    @Scheduled(fixedDelayString = "${incentivos.cron.intervalo-ms:60000}", initialDelayString = "${incentivos.cron.inicio-ms:60000}")
    public void ejecutarProcesamiento() {
        long start = System.nanoTime();
        executions.increment();
        boolean successful = true;
        var previous = MDC.getCopyOfContextMap();
        MDC.put("component", "incentivos");
        MDC.put("instanceId", System.getenv().getOrDefault("INSTANCE_ID", "local"));
            try {
            var ids = donadorRepository.findAllIds();
            pending = ids.size();
            for (String id : ids) {
                MDC.put("traceId", java.util.UUID.randomUUID().toString());
                try { fachada.procesarDonador(id); pending--; }
                catch (Exception ex) {
                    successful = false; errors.increment();
                    MDC.put("event", "cron.donador_error"); MDC.put("outcome", "error");
                    log.error("cron.donador_error donador={}", id, ex);
                }
            }
            if (successful) lastSuccess = System.currentTimeMillis() / 1000.0;
        } catch (RuntimeException ex) {
            successful = false; errors.increment();
            MDC.put("event", "cron.error"); MDC.put("outcome", "error");
            log.error("cron.error al consultar donadores", ex);
            throw ex;
            } finally {
            long elapsed = System.nanoTime() - start;
            duration.record(elapsed, java.util.concurrent.TimeUnit.NANOSECONDS);
            MDC.remove("traceId"); MDC.put("event", "cron.finalizado");
            MDC.put("outcome", successful ? "ok" : "error"); MDC.put("durationMs", String.valueOf(elapsed / 1_000_000));
            log.info("cron.finalizado pendientes={}", pending);
            MDC.clear(); if (previous != null) MDC.setContextMap(previous);
            }
        }
    }
