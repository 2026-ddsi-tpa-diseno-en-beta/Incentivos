package ar.edu.utn.dds.k3003.integracion;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;

public class FachadaDonacionesHttp implements FachadaDonaciones{
     private final RestClient restClient;

    public FachadaDonacionesHttp(String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID)
            throws NoSuchElementException {

        return restClient.get()
                .uri("/productos/{id}", productoID)
                .retrieve()
                .body(ProductoDTO.class);
    }

    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(
            String donadorID,
            LocalDate fecha)
            throws NoSuchElementException {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/donaciones/search")
                        .queryParam("donadorID", donadorID)
                        .queryParam("fecha", fecha)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<DonacionDTO>>() {});
    }

    @Override
    public void setFachadaDonadoresYEntidades(
            FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {}

    @Override
    public void setFachadaLogistica(
            FachadaLogistica fachadaLogistica) {}

    @Override
    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DonacionDTO buscarDonacionPorID(String donacionID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DonacionDTO cambiarEstadoDeDonacion(
            String donacionID,
            EstadoDonacionEnum estado) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(
            String donacionID,
            String descripcion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IdentificadorDTO agregarIdentificador(
            IdentificadorDTO identificadorDTO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(
            String identificadorID) {
        throw new UnsupportedOperationException();
    }
}
