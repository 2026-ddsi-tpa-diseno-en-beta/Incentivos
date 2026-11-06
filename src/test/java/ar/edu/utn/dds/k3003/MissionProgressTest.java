package ar.edu.utn.dds.k3003;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.*;
import ar.edu.utn.dds.k3003.model.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class MissionProgressTest {
  Mision mission(TipoMisionEnum type) {
    return MisionFactory.crear(
        "m",
        new MisionDTO(
            "m",
            "Demo",
            "b",
            CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR,
            type));
  }

  @Test
  void successfulDonationsCountAcceptedDonationsRatherThanUnits() {
    Mision m = mission(TipoMisionEnum.DONACIONES_EXITOSAS);
    assertEquals(20, m.objetivo());
    var donations =
        List.of(new DonacionSimulada("c", 1000, true), new DonacionSimulada("c", 1000, false));
    assertEquals(1, m.progreso(donations));
    assertFalse(m.estaCompleta(donations));
  }

  @Test
  void ascendingProgressResetsOnEqualOrSmallerQuantity() {
    Mision m = mission(TipoMisionEnum.DONACIONES_ASCENDENTES);
    assertEquals(5, m.objetivo());
    assertEquals(0, m.progreso(List.of()));
    var donations = new ArrayList<DonacionSimulada>();
    for (int amount : new int[] {1, 2, 3, 4, 5})
      donations.add(new DonacionSimulada("c", amount, true));
    assertEquals(5, m.progreso(donations));
    assertTrue(m.estaCompleta(donations));
    donations.add(new DonacionSimulada("c", 5, true));
    assertEquals(1, m.progreso(donations));
    assertFalse(m.estaCompleta(donations));
    donations.add(new DonacionSimulada("c", 3, true));
    assertEquals(1, m.progreso(donations));
  }

  @Test
  void revolutionaryGoalRequiresElevenDonationsAboveFiftyUnits() {
    Mision m = mission(TipoMisionEnum.REVOLUCION_DONADORA);
    assertEquals(11, m.objetivo());
    var donations =
        new ArrayList<DonacionSimulada>(
            Collections.nCopies(10, new DonacionSimulada("c", 51, true)));
    donations.add(new DonacionSimulada("c", 50, true));
    assertEquals(10, m.progreso(donations));
    assertFalse(m.estaCompleta(donations));
    donations.add(new DonacionSimulada("c", 51, true));
    assertTrue(m.estaCompleta(donations));
  }

  @Test
  void completenessCountsDistinctCategoriesAndNotRepeatedProducts() {
    Mision m = mission(TipoMisionEnum.COMPLETITUD);
    assertEquals(3, m.objetivo());
    var donations =
        List.of(
            new DonacionSimulada("a", 1, true),
            new DonacionSimulada("a", 2, true),
            new DonacionSimulada("b", 1, true));
    assertEquals(2, m.progreso(donations));
    assertFalse(m.estaCompleta(donations));
  }
}
