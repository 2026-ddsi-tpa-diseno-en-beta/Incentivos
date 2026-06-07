package ar.edu.utn.dds.k3003.repositories;

import java.util.HashMap;
import java.util.Map;


import ar.edu.utn.dds.k3003.model.DonadorIncentivo;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.Mision;

public class IncentivosRepositorio {
    private Map<String, DonadorIncentivo> donadores = new HashMap<>();
    private Map<String, Mision> misiones = new HashMap<>();
    private Map<String, Insignia> insignias = new HashMap<>();


    public void guardarDonador(DonadorIncentivo donador) {
        donadores.put(donador.getDonadorId(), donador);
    }

    public DonadorIncentivo buscarDonador(String id) {
        return donadores.get(id);
    }

    public void guardarMision(Mision mision) {
        misiones.put(mision.getId(), mision);
    }
    public Mision buscarMision(String id){
        return misiones.get(id);
    }

    public void guardarInsignia(Insignia insignia) {
        insignias.put(insignia.getId(), insignia);
    }

     public Insignia buscarInsignia(String id) {
        return insignias.get(id);
    }
    public Map<String, Insignia> getInsignias() {
        return insignias;
    }
     public Map<String, Mision> getMisiones() {
        return misiones;
    }
}
