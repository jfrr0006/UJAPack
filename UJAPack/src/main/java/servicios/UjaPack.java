package servicios;

import Utils.Estado;
import entidades.Envio;
import entidades.PuntoRuta.PuntoRuta;
import entidades.RedUjaPack;
import entidades.Registro;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UjaPack {

    /*Toda la red*/
    RedUjaPack red;
    Map<Long,Envio> envios;

    public UjaPack() throws IOException {
        red= new RedUjaPack();
        envios = new HashMap<>();

    }

    public void generarEnvio(String remitente,String destinatario,Float peso, Float dimensiones){
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
            entrada = !envio.getRuta().get(envio.getRegistroActual()).getEntrada();
        }
        envio.avanzarRegistroActual();
        envio.getRuta().get(envio.getRegistroActual()).actualizarRegistro(LocalDateTime.now(), entrada);

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

}
