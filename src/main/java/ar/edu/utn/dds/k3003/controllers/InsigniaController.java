package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;



@RestController
@RequestMapping("/insignias")
public class InsigniaController  {

  private Fachada fachada;

  public InsigniaController(Fachada fachada) {
    this.fachada = fachada;
  }

    @PostMapping
    public ResponseEntity<InsigniaDTO> crearInsignia(
            @RequestBody InsigniaDTO insigniaDTO) {

        InsigniaDTO creada = fachada.agregarInsignia(insigniaDTO);

        return ResponseEntity.status(201).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<InsigniaDTO>> getInsignias() {

        return ResponseEntity.ok(fachada.getInsignias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsigniaDTO> getInsigniaPorID(
            @PathVariable String id) {

        return ResponseEntity.ok(fachada.buscarInsigniaPorID(id));
    }

      @PostMapping("/donador/{donadorID}")
    public ResponseEntity<Void> asignarInsignia(
            @PathVariable String donadorID,
            @RequestBody InsigniaDTO insigniaDTO) {

        fachada.asignarInsigniaADonador(
                donadorID,
                insigniaDTO
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/donador/{donadorID}")
    public ResponseEntity<List<InsigniaDTO>> getInsigniasDonador(
            @PathVariable String donadorID) {

        return ResponseEntity.ok(
                fachada.getInsigniasDeDonador(donadorID)
        );
    }

}
