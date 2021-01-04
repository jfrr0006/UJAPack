package beans;

import DTO.EnvioDTO;
import entidades.Envio;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

@Service
public class Mapeador {

    public EnvioDTO aEnvioDTO(Envio envio) {

        EnvioDTO envioDTO = new EnvioDTO(
                envio.getId(),
                envio.getImporte(),
                envio.getPeso(),
                envio.getDimensiones(),
                envio.getRemitente(),
                envio.getDestinatario(),
                envio.getDatos_remitente(),
                envio.getDatos_destinatario(),
                envio.getEstado(),
                envio.getRegistroActual());
        //   envio.getNotificacion(),
        //   envio.getDatosNotificacion());


        envioDTO.setRuta(WebMvcLinkBuilder.
                linkTo(WebMvcLinkBuilder.methodOn(ServicioRestAPI.class).
                        detalleRutaEnvio(envioDTO.getId())).
                withSelfRel());


        return envioDTO;

    }


}
