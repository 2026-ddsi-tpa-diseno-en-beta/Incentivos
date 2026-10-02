package ar.edu.utn.dds.k3003.controllers;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NoSuchElementException.class)
  ResponseEntity<Map<String,String>> notFound(NoSuchElementException e) { return error(404,e.getMessage()); }
  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Map<String,String>> invalid(IllegalArgumentException e) { return error(400,e.getMessage()); }
  @ExceptionHandler(RestClientException.class)
  ResponseEntity<Map<String,String>> remote(RestClientException e) { return error(502,"No se pudo completar la comunicación con otro componente"); }
  @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
      org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
      org.springframework.web.bind.MissingServletRequestParameterException.class})
  ResponseEntity<Map<String,String>> malformed(Exception e) { return error(400,"Datos o parámetros inválidos"); }
  private ResponseEntity<Map<String,String>> error(int status, String message) {
    return ResponseEntity.status(status).body(Map.of("error",message == null ? "Error" : message));
  }
}
