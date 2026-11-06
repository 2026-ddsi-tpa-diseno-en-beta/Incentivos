package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "misiones")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_mision")
public abstract class Mision {
  @Id private String id;
  private String nombre;
  private String insigniaID;

  @Enumerated(EnumType.STRING)
  private CategoriaDonadorEnum categoriaInicio;

  @Enumerated(EnumType.STRING)
  private CategoriaDonadorEnum categoriaFin;

  @Enumerated(EnumType.STRING)
  private TipoMisionEnum tipo;

  protected Mision() {}

  public Mision(
      String id,
      String nombre,
      String insigniaID,
      CategoriaDonadorEnum inicio,
      CategoriaDonadorEnum fin,
      TipoMisionEnum tipo) {
    this.id = id;
    this.nombre = nombre;
    this.insigniaID = insigniaID;
    this.categoriaInicio = inicio;
    this.categoriaFin = fin;
    this.tipo = tipo;
  }

  public abstract boolean estaCompleta(List<DonacionSimulada> donaciones);

  public int objetivo() {
    return switch (tipo) {
      case COMPLETITUD -> 3;
      case DONACIONES_EXITOSAS -> 20;
      case DONACIONES_ASCENDENTES -> 5;
      case REVOLUCION_DONADORA -> 11;
    };
  }

  public int progreso(List<DonacionSimulada> donaciones) {
    return switch (tipo) {
      case COMPLETITUD -> (int)
          donaciones.stream().map(DonacionSimulada::getCategoria).distinct().count();
      case DONACIONES_EXITOSAS -> (int)
          donaciones.stream().filter(DonacionSimulada::fueAceptada).count();
      case REVOLUCION_DONADORA -> (int)
          donaciones.stream().filter(d -> d.getCantidad() > 50).count();
      case DONACIONES_ASCENDENTES -> {
        int count = donaciones.isEmpty() ? 0 : 1;
        for (int i = donaciones.size() - 1; i > 0 && count < 5; i--) {
          if (donaciones.get(i).getCantidad() <= donaciones.get(i - 1).getCantidad()) break;
          count++;
        }
        yield count;
      }
    };
  }

  public String getId() {
    return id;
  }
  ;

  public String getNombre() {
    return nombre;
  }

  public String getInsigniaID() {
    return insigniaID;
  }

  public CategoriaDonadorEnum getCategoriaInicio() {
    return categoriaInicio;
  }

  public CategoriaDonadorEnum getCategoriaFin() {
    return categoriaFin;
  }

  public TipoMisionEnum getTipo() {
    return tipo;
  }

  @PrePersist
  private void generarId() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
  }

  public void modificarNombre(String nombre) {
    if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre requerido");
    this.nombre = nombre;
  }
}
