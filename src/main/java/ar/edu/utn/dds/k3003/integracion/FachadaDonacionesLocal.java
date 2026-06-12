package ar.edu.utn.dds.k3003.integracion;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;

public class FachadaDonacionesLocal implements FachadaDonaciones{
   @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(
            String donadorID,
            LocalDate fecha) throws NoSuchElementException {

        return List.of();
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID)
            throws NoSuchElementException {

        return new ProductoDTO(
                productoID,
                "Producto Local",
                "Producto simulado",
                "CAT-1",
                "ID-1"
        );
    }

    @Override
    public void setFachadaDonadoresYEntidades(
            FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
    }

    @Override
    public void setFachadaLogistica(
            FachadaLogistica fachadaLogistica) {
    }

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
