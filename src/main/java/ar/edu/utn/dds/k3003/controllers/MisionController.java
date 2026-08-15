package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/misiones")
public class MisionController {
     
     private Fachada fachada;

    public MisionController(Fachada fachada) {
        this.fachada = fachada;
}

@PostMapping
public ResponseEntity<MisionDTO> crearMision(
            @RequestBody MisionDTO dto) {

        MisionDTO creada = fachada.agregarMision(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    @GetMapping
    public ResponseEntity<List<MisionDTO>> getMisiones() {

        return ResponseEntity.ok(fachada.getMisiones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MisionDTO> getMisionByID(
            @PathVariable String id) {

        return ResponseEntity.ok(fachada.buscarMisionPorID(id));
    }

     @PostMapping("/donador/{donadorID}")
    public ResponseEntity<Void> asignarMision(
            @PathVariable String donadorID,
            @RequestBody MisionDTO misionDTO) {

        fachada.asignarMisionADonador(
                donadorID,
                misionDTO
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/donador/{donadorID}")
    public ResponseEntity<MisionDTO> getMisionEnCurso(
            @PathVariable String donadorID) {

        try {
        MisionDTO mision = fachada.getMisionEnCursoDeDonador(donadorID);
        return ResponseEntity.ok(mision);
        } catch (NoSuchElementException e) {
        return ResponseEntity.noContent().build();
        }
    }
}