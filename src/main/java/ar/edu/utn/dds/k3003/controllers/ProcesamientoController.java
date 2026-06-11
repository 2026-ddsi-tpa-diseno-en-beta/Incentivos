package ar.edu.utn.dds.k3003.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.utn.dds.k3003.Fachada;

@RestController
@RequestMapping("/procesamiento")
public class ProcesamientoController {

    
    private Fachada fachada;

    public ProcesamientoController(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping("/{donadorID}")
    public ResponseEntity<Void> procesarDonador(
            @PathVariable String donadorID) {

        fachada.procesarDonador(donadorID);

        return ResponseEntity.noContent().build();
    }
}