package ar.edu.utn.dds.k3003.fachadasExternas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.integracion.FachadaDonacionesHttp;
import ar.edu.utn.dds.k3003.integracion.FachadaDonacionesLocal;
import ar.edu.utn.dds.k3003.integracion.FachadaDonadoresYEntidadesHttp;
import ar.edu.utn.dds.k3003.integracion.FachadaDonadoresYEntidadesLocal;

@Configuration
public class FachadasExternasConfig {
    public FachadasExternasConfig(
            Fachada fachada,
            @Value("${donatrack.donadores-y-entidades.url:}") String donadoresUrl,
            @Value("${donatrack.donaciones.url:}") String donacionesUrl) {

        fachada.setFachadaDonadoresYEntidades(
                tieneUrl(donadoresUrl)
                        ? new FachadaDonadoresYEntidadesHttp(donadoresUrl)
                        : new FachadaDonadoresYEntidadesLocal());

        fachada.setFachadaDonaciones(
                tieneUrl(donacionesUrl)
                        ? new FachadaDonacionesHttp(donacionesUrl)
                        : new FachadaDonacionesLocal());
    }

    private Boolean tieneUrl(String url) {
        return url != null && !url.isBlank();
    }
}
