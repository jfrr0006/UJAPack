package servicios;

import entidades.Envio;
import entidades.PuntoRuta.PuntoRuta;
import entidades.RedUjaPack;
import entidades.Registro;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

        }
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        Envio nuevoEnvio = new Envio(number,calcularCosto(ruta.size(),peso,dimensiones),registros,peso,dimensiones,remitente,destinatario);

        envios.put(number,nuevoEnvio);




    }

    public Float calcularCosto(int numPuntosRuta, Float peso, Float dimensiones){
        return (peso*dimensiones*(numPuntosRuta+1))/1000;

    }

}
