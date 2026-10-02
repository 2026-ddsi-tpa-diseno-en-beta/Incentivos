# Incentivos: ejecución y cambios de entrega 5

Requiere Java 21 y Maven. Validación: `mvn package`. En Logística `verify` también exige 80% de cobertura; no se cambió ese umbral.

Cada donador tiene progreso propio sobre el catálogo. El procesamiento sincroniza categoría con Donadores, entrega insignias una sola vez y revoca Donaciones Exitosas cuando baja de veinte aceptadas. El cron registra fallos por donador.

Configurar las URLs de Donaciones y Donadores definidas en application.properties. En producción usar variables de entorno para conexión, usuario y contraseña de PostgreSQL y para las integraciones. No reemplazar application.properties de producción con las configuraciones H2 de prueba.

Swagger: `/swagger-ui/index.html`; contrato: `/v3/api-docs`; salud: `/actuator/health`; métricas: `/actuator/prometheus`. Los cuatro componentes propagan `X-Trace-Id`. Para logs centralizados configurar `BETTERSTACK_SOURCE_TOKEN` y `BETTERSTACK_INGEST_URL`; verificar la recepción en la cuenta del equipo.

La integración de los seis flujos está en el repo testing, `local/probar_integracion.py`, y requiere los cuatro repos como carpetas hermanas. La suite usa H2 aislado y simula callbacks del worker; no valida un broker externo. MCP está como módulo independiente en `mcp-server` y el bot en telegramBot.

El despliegue todavía requiere probar la base existente, las credenciales reales y las URLs públicas. Una llamada HTTP entre servicios no participa de la transacción de la base local: si falla otro componente durante una operación, revisar el resultado con el traceId antes de reintentar.

Configuración completa del ensayo externo: `testing/SETUP_PRESENTACION.md`. Datadog se habilita explícitamente con `DATADOG_ENABLED`; Prometheus permite validar métricas sin credenciales de un proveedor.
