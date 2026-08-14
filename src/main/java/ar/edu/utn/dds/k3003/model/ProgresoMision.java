package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "progreso_mision")
public class ProgresoMision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String donadorId;

    private String misionId;

    private boolean completada = false;

    public ProgresoMision() {
    }

    public ProgresoMision(String donadorId, String misionId) {
        this.donadorId = donadorId;
        this.misionId = misionId;
        this.completada = false;
    }

    public Long getId() {
        return id;
    }

    public String getDonadorId() {
        return donadorId;
    }

    public String getMisionId() {
        return misionId;
    }

    public boolean estaCompletada() {
        return completada;
    }

    public void completar() {
        this.completada = true;
    }

    public void descompletar() {
        this.completada = false;
    }
}