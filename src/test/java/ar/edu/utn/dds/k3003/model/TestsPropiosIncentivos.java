package ar.edu.utn.dds.k3003.model;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class TestsPropiosIncentivos {
     private Fachada fachada;
    private FachadaDonadoresYEntidades mockDonadores;
    private FachadaDonaciones mockDonaciones;

    @BeforeEach
    void setUp() {
        fachada = new Fachada();

        mockDonadores = mock(FachadaDonadoresYEntidades.class);
        mockDonaciones = mock(FachadaDonaciones.class);

        fachada.setFachadaDonadoresYEntidades(mockDonadores);
        fachada.setFachadaDonaciones(mockDonaciones);

        when(mockDonadores.buscarDonadorPorID("123")).thenReturn(null);
    }

    @Test
    @DisplayName("Se le pueden asignar varias insignias a un donador")
    void donadorTieneVariasInsignias() {
        InsigniaDTO i1 = fachada.agregarInsignia(new InsigniaDTO(null, "Primera Insignia", "Descripcion1"));
        InsigniaDTO i2 = fachada.agregarInsignia(new InsigniaDTO(null, "Segunda Insignia", "Descripcion2"));
        InsigniaDTO i3 = fachada.agregarInsignia(new InsigniaDTO(null, "Tercera Insignia", "Descripcion3"));

        fachada.asignarInsigniaADonador("123", i1);
        fachada.asignarInsigniaADonador("123", i2);
        fachada.asignarInsigniaADonador("123", i3);

        List<InsigniaDTO> resultado = fachada.getInsigniasDeDonador("123");

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("El donador NO completa la misión si no alcanza las donaciones")
    void siNoAlcanzaLasDonacionesNoSeCompletaLaMision() {
        MisionDTO mision = fachada.agregarMision(
            new MisionDTO(null, "Donaciones Exitosas", null,  
            CategoriaDonadorEnum.OCASIONAL, 
            CategoriaDonadorEnum.COLABORADOR,
            TipoMisionEnum.DONACIONES_EXITOSAS)
        );

        fachada.asignarMisionADonador("123", mision);
        List<DonacionDTO> donaciones = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            donaciones.add(new DonacionDTO(
                "id" + i,
                "123",
                "dep",
                "desc",
                "prod",
                1,
                EstadoDonacionEnum.ACEPTADA
            ));
        }

        when(mockDonaciones.buscarPorDonadorYFechaInicio(any(), any()))
            .thenReturn(donaciones);

         when(mockDonaciones.buscarProductoPorID(any()))
        .thenReturn(
            new ProductoDTO(
                "1",
                "prod",
                "Producto",
                "categoria1",
                "subcategoria1"
            )
        );

        fachada.procesarDonador("123");

        assertNotNull(fachada.getMisionEnCursoDeDonador("123"));
    }

    @Test
    @DisplayName("Le asigno una misión a un donador y me la muestra como 'en curso', ya que de momento cada categoria cuenta con una unica mision para ser completada, entonces deberia ser la unica en curso en la lista de misiones del donador")
    void testAsignarMisionYConsultarEnCurso() {
        String donadorId = "123";
        MisionDTO misionDto = fachada.agregarMision(new MisionDTO(null, "Mision1", "insignia1", 
                CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR, TipoMisionEnum.DONACIONES_EXITOSAS));
        
        when(mockDonadores.buscarDonadorPorID(donadorId)).thenReturn(null);

        fachada.asignarMisionADonador(donadorId, misionDto);
        MisionDTO enCurso = fachada.getMisionEnCursoDeDonador(donadorId);

        assertNotNull(enCurso);
        assertEquals("Mision1", enCurso.nombre());
    }

    @Test
    @DisplayName("El donador completa la mision de Donaciones Exitosas y recibe correctamente la insignia")
    void completarMisionAgregaInsignia(){
        InsigniaDTO insignia = fachada.agregarInsignia(
        new InsigniaDTO(null, "Insignia", "descripcion")
    );

    MisionDTO mision = fachada.agregarMision(
        new MisionDTO(null, "Donaciones Exitosas", insignia.id(),  CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR, TipoMisionEnum.DONACIONES_EXITOSAS)
    );

    fachada.asignarMisionADonador("123", mision);
    List<DonacionDTO> donaciones = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
        donaciones.add(new DonacionDTO(
            "id" + i,   
            "123",
            null,
            null,
            "prod",
            1,
            EstadoDonacionEnum.ACEPTADA
        ));
    }

    when(mockDonaciones.buscarPorDonadorYFechaInicio(any(), any()))
        .thenReturn(donaciones);

    when(mockDonaciones.buscarProductoPorID(any()))
        .thenReturn(
            new ProductoDTO(
                "1",
                "prod",
                "Producto Test",
                "categoria1",
                "subcategoria1"
            )
        );


    fachada.procesarDonador("123");

    List<InsigniaDTO> insignias = fachada.getInsigniasDeDonador("123");

    assertEquals(1, insignias.size());

    }
    
    @Test
@DisplayName("El donador completa la misión de donaciones ascendentes")
void completarMisionDonacionesAscendentes() {

    InsigniaDTO insignia = fachada.agregarInsignia(
        new InsigniaDTO(null, "Ascendente", "descripcion")
    );

    MisionDTO mision = fachada.agregarMision(
        new MisionDTO(
            null,
            "Ascendentes",
            insignia.id(),
            CategoriaDonadorEnum.COLABORADOR,
            CategoriaDonadorEnum.SALVADOR,
            TipoMisionEnum.DONACIONES_ASCENDENTES
        )
    );

    fachada.asignarMisionADonador("123", mision);

    List<DonacionDTO> donaciones = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {

        donaciones.add(
            new DonacionDTO(
                "id" + i,
                "123",
                null,
                null,
                "prod",
                i,
                EstadoDonacionEnum.ACEPTADA
            )
        );
    }

    when(mockDonaciones.buscarPorDonadorYFechaInicio(any(), any()))
        .thenReturn(donaciones);

    when(mockDonaciones.buscarProductoPorID(any()))
        .thenReturn(
            new ProductoDTO(
                "1",
                "prod",
                "Producto",
                "categoria",
                "subcategoria"
            )
        );

    fachada.procesarDonador("123");

    assertEquals(
        1,
        fachada.getInsigniasDeDonador("123").size()
    );
}

    @Test
@DisplayName("Completa la misión revolución donadora")
void completarMisionRevolucionDonadora() {

    InsigniaDTO insignia = fachada.agregarInsignia(
        new InsigniaDTO(null, "Revolucionario", "descripcion")
    );

    MisionDTO mision = fachada.agregarMision(
        new MisionDTO(
            null,
            "Revolución",
            insignia.id(),
            CategoriaDonadorEnum.TRANSFORMADOR,
            CategoriaDonadorEnum.REVOLUCIONARIO,
            TipoMisionEnum.REVOLUCION_DONADORA
        )
    );

    fachada.asignarMisionADonador("123", mision);

    List<DonacionDTO> donaciones = new ArrayList<>();

    for (int i = 0; i < 11; i++) {

        donaciones.add(
            new DonacionDTO(
                "id" + i,
                "123",
                null,
                null,
                "prod",
                60,
                EstadoDonacionEnum.ACEPTADA
            )
        );
    }

    when(mockDonaciones.buscarPorDonadorYFechaInicio(any(), any()))
        .thenReturn(donaciones);

    when(mockDonaciones.buscarProductoPorID(any()))
        .thenReturn(
            new ProductoDTO(
                "1",
                "prod",
                "Producto",
                "categoria",
                "subcategoria"
            )
        );

    fachada.procesarDonador("123");

    assertEquals(
        1,
        fachada.getInsigniasDeDonador("123").size()
    );
}

}

