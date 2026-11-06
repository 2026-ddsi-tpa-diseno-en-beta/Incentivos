package ar.edu.utn.dds.k3003;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ar.edu.utn.dds.k3003.app.Application;
import com.fasterxml.jackson.databind.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.*;

@SpringBootTest(
    classes = Application.class,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:incentivos6;DB_CLOSE_DELAY=-1",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "donatrack.read-model.publisher.enabled=true",
      "READ_MODEL_SEND_INITIAL_DELAY_MS=3600000",
      "READ_MODEL_ADMIN_KEY=test-key",
      "incentivos.cron.inicio-ms=3600000",
      "spring.rabbitmq.dynamic=false",
      "management.health.rabbit.enabled=false",
      "management.datadog.metrics.export.enabled=false"
    })
@AutoConfigureMockMvc
@org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApiReadModelTest {
  static final HttpServer remote;
  static final ObjectMapper JSON = new ObjectMapper();
  static final AtomicInteger accepted = new AtomicInteger(20);
  static final AtomicBoolean large = new AtomicBoolean();
  static final AtomicReference<String> category = new AtomicReference<>("OCASIONAL");

  static {
    try {
      remote = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      remote.createContext(
          "/",
          e -> {
            String path = e.getRequestURI().getPath();
            Object body;
            if (path.startsWith("/productos/"))
              body =
                  Map.of(
                      "id",
                      path.substring(path.lastIndexOf('/') + 1),
                      "nombre",
                      "Producto",
                      "descripcion",
                      "Demo",
                      "categoriaID",
                      "c" + path.substring(path.lastIndexOf('/') + 1));
            else if (path.equals("/donaciones")) {
              List<Object> donations = new ArrayList<>();
              for (int i = 0; i < 25; i++)
                donations.add(
                    Map.of(
                        "id",
                        "x" + i,
                        "donadorID",
                        "d",
                        "depositoID",
                        "z",
                        "productoID",
                        "" + (i % 3),
                        "descripcion",
                        "Demo",
                        "cantidad",
                        large.get() ? 60 : i + 1,
                        "estado",
                        i < accepted.get() ? "ACEPTADA" : "CONQUEJA"));
              body = donations;
            } else {
              if (e.getRequestMethod().equals("PATCH"))
                category.set(JSON.readTree(e.getRequestBody()).path("categoria").asText());
              body =
                  Map.of(
                      "id",
                      "d",
                      "nombre",
                      "Ana",
                      "apellido",
                      "Demo",
                      "edad",
                      25,
                      "estado",
                      "VERIFICADO",
                      "categoria",
                      category.get());
            }
            byte[] out = JSON.writeValueAsBytes(body);
            e.getResponseHeaders().add("Content-Type", "application/json");
            e.sendResponseHeaders(200, out.length);
            e.getResponseBody().write(out);
            e.close();
          });
      remote.start();
    } catch (Exception ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  @DynamicPropertySource
  static void urls(DynamicPropertyRegistry registry) {
    for (String name : List.of("donatrack.donaciones.url", "donatrack.donadores-y-entidades.url"))
      registry.add(name, () -> "http://127.0.0.1:" + remote.getAddress().getPort());
  }

  @AfterAll
  static void close() {
    remote.stop(0);
  }

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper mapper;
  @Autowired JdbcTemplate jdbc;
  @Autowired Fachada facade;

  JsonNode call(String method, String path, Object body) throws Exception {
    var request =
        request(org.springframework.http.HttpMethod.valueOf(method), path)
            .contentType(MediaType.APPLICATION_JSON);
    if (body != null) request.content(mapper.writeValueAsString(body));
    var response = mvc.perform(request).andReturn().getResponse();
    assertTrue(
        response.getStatus() < 300, response.getStatus() + " " + response.getContentAsString());
    String content = response.getContentAsString();
    return content.isBlank() || !content.startsWith("{") && !content.startsWith("[")
        ? mapper.nullNode()
        : mapper.readTree(content);
  }

  @Test
  void allMissionTypesPublishNumericProgressAndKeepRewardLifecycle() throws Exception {
    facade.limpiarDatos();
    accepted.set(20);
    category.set("OCASIONAL");
    large.set(false);
    String[][] steps = {
      {"COMPLETITUD", "OCASIONAL", "COLABORADOR"},
      {"DONACIONES_EXITOSAS", "COLABORADOR", "TRANSFORMADOR"},
      {"REVOLUCION_DONADORA", "TRANSFORMADOR", "REVOLUCIONARIO"}
    };
    List<String> missions = new ArrayList<>(), badges = new ArrayList<>();
    for (String[] step : steps) {
      String badge =
          call("POST", "/insignias", Map.of("nombre", step[0], "descripcion", "Demo"))
              .path("id")
              .asText();
      badges.add(badge);
      String mission =
          call(
                  "POST",
                  "/misiones",
                  Map.of(
                      "nombre",
                      step[0],
                      "insigniaID",
                      badge,
                      "categoriaInicio",
                      step[1],
                      "categoriaFin",
                      step[2],
                      "tipo",
                      step[0]))
              .path("id")
              .asText();
      missions.add(mission);
      call(
          "PUT",
          "/misiones/" + mission,
          Map.of(
              "nombre",
              step[0] + " Demo",
              "insigniaID",
              badge,
              "categoriaInicio",
              step[1],
              "categoriaFin",
              step[2],
              "tipo",
              step[0]));
      call("POST", "/misiones/donador/d", Map.of("id", mission));
      call("GET", "/misiones/donador/d", null);
      if (step[0].equals("REVOLUCION_DONADORA")) {
        call("POST", "/procesamiento/d", null);
        String content =
            jdbc
                .queryForList(
                    "select content from read_model_outbox order by id desc", String.class)
                .stream()
                .filter(x -> x.contains("misionEnCurso"))
                .findFirst()
                .orElseThrow();
        assertEquals(
            11,
            mapper
                .readTree(content)
                .path("payload")
                .path("misionEnCurso")
                .path("objetivo")
                .asInt());
        large.set(true);
      }
      call("POST", "/procesamiento/d", null);
      assertEquals(step[2], category.get());
      call("GET", "/insignias/donador/d", null);
      call("GET", "/misiones/" + mission, null);
      call("GET", "/insignias/" + badge, null);
      call(
          "PUT",
          "/insignias/" + badge,
          Map.of("nombre", "Premio Demo", "descripcion", "Actualizado"));
    }
    assertEquals(3, call("GET", "/insignias/donador/d", null).size());
    call("POST", "/insignias/donador/d", Map.of("id", badges.getFirst()));
    accepted.set(19);
    call("POST", "/procesamiento/d", null);
    assertEquals("COLABORADOR", category.get());
    // Only Completitud survives: Revolución depended on Exitosas.
    var remaining = call("GET", "/insignias/donador/d", null);
    assertEquals(1, remaining.size());
    assertEquals(badges.getFirst(), remaining.get(0).path("id").asText());
    assertEquals(missions.get(1), call("GET", "/misiones/donador/d", null).path("id").asText());
    call("POST", "/procesamiento/d", null);
    assertEquals("COLABORADOR", category.get());
    assertEquals(1, call("GET", "/insignias/donador/d", null).size());
    for (String path : List.of("/misiones", "/insignias", "/actuator/prometheus"))
      call("GET", path, null);
    mvc.perform(post("/admin/read-model/republicar").header("X-Admin-Key", "test-key"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    facade.limpiarDatos();
    String badge =
        call("POST", "/insignias", Map.of("nombre", "Borrable", "descripcion", "Demo"))
            .path("id")
            .asText();
    String mission =
        call(
                "POST",
                "/misiones",
                Map.of(
                    "nombre",
                    "Borrable",
                    "insigniaID",
                    badge,
                    "categoriaInicio",
                    "OCASIONAL",
                    "categoriaFin",
                    "COLABORADOR",
                    "tipo",
                    "COMPLETITUD"))
            .path("id")
            .asText();
    call("DELETE", "/misiones/" + mission, null);
    call("DELETE", "/insignias/" + badge, null);
    assertTrue(
        jdbc.queryForObject(
                "select count(*) from read_model_outbox where content like '%DELETE%'",
                Integer.class)
            > 0);
  }

  @Test
  void ascendingBranchReachesSalvadorAndRevolutionWithoutExitosas() throws Exception {
    facade.limpiarDatos();
    accepted.set(20);
    category.set("OCASIONAL");
    large.set(false);
    for (String[] step :
        new String[][] {
          {"COMPLETITUD", "OCASIONAL", "COLABORADOR"},
          {"DONACIONES_ASCENDENTES", "COLABORADOR", "SALVADOR"},
          {"REVOLUCION_DONADORA", "SALVADOR", "REVOLUCIONARIO"}
        }) {
      String badge =
          call("POST", "/insignias", Map.of("nombre", step[0], "descripcion", "Demo"))
              .path("id")
              .asText();
      String mission =
          call(
                  "POST",
                  "/misiones",
                  Map.of(
                      "nombre",
                      step[0],
                      "insigniaID",
                      badge,
                      "categoriaInicio",
                      step[1],
                      "categoriaFin",
                      step[2],
                      "tipo",
                      step[0]))
              .path("id")
              .asText();
      call("POST", "/misiones/donador/d", Map.of("id", mission));
      if (step[0].equals("REVOLUCION_DONADORA")) large.set(true);
      call("POST", "/procesamiento/d", null);
      assertEquals(step[2], category.get());
    }
    assertEquals(3, call("GET", "/insignias/donador/d", null).size());
  }

  @Test
  void absentRewardsAndMissionAreReportedAs404() throws Exception {
    for (String resource : List.of("insignias", "misiones")) {
      mvc.perform(get("/" + resource + "/donador/sin-asignaciones"))
          .andExpect(
              org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                  .isNotFound());
    }
  }
}
