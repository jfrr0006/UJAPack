package servicios;

import Utils.Estado;
import entidades.Envio;
import entidades.PuntoRuta.PuntoRuta;
import entidades.RedUjaPack;
import entidades.Registro;
import excepciones.DirNotificacionIncorrecta;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
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
    Map<Long, Envio> envios;


    public UjaPack() {
        red = new RedUjaPack();
        envios = new HashMap<>();

    }

    public Envio generarEnvio(@NotBlank String remitente, @NotBlank String destinatario, @Positive Float peso, @Positive Float dimensiones) {

        List<PuntoRuta> ruta = red.listaRutaMinima(remitente, destinatario);
        List<Registro> registros = new ArrayList<>();

        for (PuntoRuta punto : ruta) {
            registros.add(new Registro(punto));
            registros.add(new Registro(punto));

        }
        registros.add(new Registro(ruta.get(ruta.size() - 1)));//Metemos el ultimo de nuevo por 3º vez para el registro entregado
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        Envio nuevoEnvio = new Envio(number, calcularCosto(ruta.size(), peso, dimensiones), registros, peso, dimensiones, remitente, destinatario);

        envios.put(number, nuevoEnvio);

        return nuevoEnvio;
    }

    private Float calcularCosto(int numPuntosRuta, Float peso, Float dimensiones) {
        return (peso * dimensiones * (numPuntosRuta + 1)) / 1000;

    }

    public void avanzarEnvios() {//Todos los que no esten ya entregados
        for (Envio envio : envios.values()) {
            if (envio.getEstado() != Estado.Entregado) {
                registroES(envio);
                actualizarEstadoEnvio(envio);
            }

        }
    }

    private void registroES(Envio envio) {
        Boolean entrada;
        if (envio.getRegistroActual() == 0) {
            entrada = true;
        } else {
            entrada = !envio.getRuta().get(envio.getRegistroActual() - 1).getEntrada();
        }

        envio.getRuta().get(envio.getRegistroActual()).actualizarRegistro(LocalDateTime.now(), entrada);

        if (envio.getNotificacion().equals(envio.getRuta().get(envio.getRegistroActual()).getPuntoR().getLugar())) {
            //Ha llegado al punto de notificacion
            mandarNotificacion(envio);
        }
        envio.avanzarRegistroActual();

    }


    private void actualizarEstadoEnvio(Envio envio) {
        //Actualizar Estado
        if (envio.getRegistroActual() == envio.getRuta().size() - 2) {
            envio.setEstado(Estado.EnReparto);

        }
        if (envio.getRegistroActual() == envio.getRuta().size() - 1) {
            envio.setEstado(Estado.Entregado);

        }
    }

    private String mandarNotificacion(Envio envio) {
        //Antes sacabamos por pantalla pero hemos rectificado aunque aun no se usa el valor devuelto se espera usarse en un futuro
        String es;
        if (envio.getRuta().get(envio.getRegistroActual()).getEntrada()) {
            es = "ha entrado a ";
        } else {
            es = "ha salido de ";
        }

        return "El envio con identificador " + envio.getId() + " " + es + envio.getNotificacion();
    }

    public void activarNotificacion(long idenvio, String noti) {
        Boolean existe = false;
        for (Registro regis : envios.get(idenvio).getRuta()) {
            if (noti.equals(regis.getPuntoR().getLugar())) {
                existe = true;
                break;
            }
        }
        if (existe) {
            envios.get(idenvio).setNotificacion(noti);
        } else {
            throw new DirNotificacionIncorrecta();
        }

    }

    public String situacionActualEnvio(long idenvio) {
        String es;
        List<Registro> ruta = envios.get(idenvio).getRuta();
        int registroActual = envios.get(idenvio).getRegistroActual() - 1;
        if (ruta.get(registroActual).getEntrada()) {
            es = "Ha entrado a ";
        } else {
            es = "Ha salido de ";
        }
        return "Ubicacion: " + es + ruta.get(registroActual).getPuntoR().getLugar() + " Hora: " + ruta.get(registroActual).getFecha().toString();

    }

    public List<String> listadoRutaEnvio(long idenvio) {
        String es;
        List<String> registros = new ArrayList<>();
        int cont = 0;
        List<Registro> ruta = envios.get(idenvio).getRuta();

        while (ruta.get(cont).getFecha() != null) {

            if (ruta.get(cont).getEntrada()) {
                es = "Ha entrado a ";
            } else {
                es = "Ha salido de ";
            }
            registros.add("Ubicacion: " + es + ruta.get(cont).getPuntoR().getLugar() + " Hora: " + ruta.get(cont).getFecha().toString());
            cont++;
        }

        return registros;
    }


}
