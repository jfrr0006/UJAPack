package servicios;

import entidades.Envio;
import entidades.PuntoRuta.PuntoRuta;
import entidades.RedUjaPack;
import entidades.Registro;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import utils.Estado;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class UjaPack {

    /* Toda la red de puntos de control */
    RedUjaPack red;
    /*Mapa con la lista de Envios ordenada por ID*/
    Map<Long,Envio> envios;

    public UjaPack() throws IOException {
        red= new RedUjaPack();
        envios = new HashMap<>();

    }

    public void generarEnvio(@NotBlank @Valid String remitente, @NotBlank @Valid String destinatario, @Positive @Valid Float peso, @Positive @Valid Float dimensiones){
   //     if(peso<0 || dimensiones<0){
   //         throw new DimensionesPesoIncorrectos();
   //     }

        List<PuntoRuta> ruta = red.listaRutaMinima(remitente,destinatario);
        List<Registro> registros = new ArrayList<>();

        for (PuntoRuta punto: ruta) {
            registros.add(new Registro(punto));
            registros.add(new Registro(punto));

        }
        registros.add(new Registro(ruta.get(ruta.size()-1)));//Metemos el ultimo de nuevo por 3º vez para el registro entregado
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        Envio nuevoEnvio = new Envio(number,calcularCosto(ruta.size(),peso,dimensiones),registros,peso,dimensiones,remitente,destinatario);

        envios.put(number,nuevoEnvio);


    }

    private Float calcularCosto(int numPuntosRuta, Float peso, Float dimensiones){
        return (peso*dimensiones*(numPuntosRuta+1))/1000;

    }

    public void avanzarEnvios(){//todos los que no esten ya entregados
        for (Envio envio : envios.values()) {
            if (envio.getEstado() != Estado.Entregado) {
                registroES(envio);
                actualizarEstadoEnvio(envio);
            }
        }
    }

    private void registroES(Envio envio){
        Boolean entrada;
        if (envio.getRegistroActual() == 0) {
            entrada = true;
        } else {
            entrada = !envio.getRuta().get(envio.getRegistroActual()-1).getEntrada();
        }
        envio.getRuta().get(envio.getRegistroActual()).actualizarRegistro(LocalDateTime.now(), entrada);
        envio.avanzarRegistroActual();


    }

    private void actualizarEstadoEnvio(Envio envio){
        //Actualizar Estado
        if(envio.getRegistroActual()==envio.getRuta().size()-2){
            envio.setEstado(Estado.EnReparto);

        }
        if(envio.getRegistroActual()==envio.getRuta().size()-1){
            envio.setEstado(Estado.Entregado);

        }


    }
    public void mostrarPrueba(){
        for (Envio envio : envios.values()) {
            System.out.println(envio.getImporte()+"  "+envio.getRuta().size());

        }

    }

}
