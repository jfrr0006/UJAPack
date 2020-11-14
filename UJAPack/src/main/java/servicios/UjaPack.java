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
import java.time.temporal.ChronoUnit;

@Service
@Validated
public class UjaPack {

    /* Toda la red de puntos de control */
    RedUjaPack red;
    /*Mapa con la lista de Envios ordenada por ID*/
    Map<Long, Envio> envios;
    /*Mapa con la lista de Envios Extraviados ordenada por ID*/
    Map<Long, Envio> enviosExtraviados;


    public UjaPack() {
        red = new RedUjaPack();
        envios = new HashMap<>();
        enviosExtraviados = new HashMap<>();
    }

    /**
     * Genera un nuevo envio
     * @param remitente Nombre del lugar de Inicio del envio
     * @param destinatario Nombre del lugar de Finalizacion del envio
     * @param _datos_remitente Datos de la persona que realiza el envio
     * @param _datos_destinatario Datos de la persona que recibe el envio
     * @param peso Peso del paquete
     * @param dimensiones Dimensiones del paquete
     * @return Nuevo envio creado
     */
    public Envio generarEnvio(@NotBlank String remitente, @NotBlank String destinatario, @Positive Float peso, @Positive Float dimensiones,@NotBlank String _datos_remitente, @NotBlank String _datos_destinatario) {

        List<PuntoRuta> ruta = red.listaRutaMinima(remitente, destinatario);
        List<Registro> registros = new ArrayList<>();

        for (PuntoRuta punto : ruta) {
            registros.add(new Registro(punto));
            registros.add(new Registro(punto));

        }
        registros.add(new Registro(ruta.get(ruta.size() - 1)));//Metemos el ultimo de nuevo por 3º vez para el registro entregado
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        Envio nuevoEnvio = new Envio(number, calcularCosto(ruta.size(), peso, dimensiones), registros, peso, dimensiones, remitente, destinatario,_datos_remitente,_datos_destinatario);

        envios.put(number, nuevoEnvio);

        return nuevoEnvio;
    }

    private Float calcularCosto(int numPuntosRuta, Float peso, Float dimensiones) {
        return (peso * dimensiones * (numPuntosRuta + 1)) / 1000;

    }

    /**
     * Avanza todos los envios que no esten ya en estado de Entregado
     */
    public void avanzarEnvios() {
        for (Envio envio : envios.values()) {
            if (envio.getEstado() != Estado.Entregado) {
                registroES(envio);
                actualizarEstadoEnvio(envio);
            }

        }
    }

    /**
     * Actualiza el estado actual del envio dependiendo de en que punto de la ruta se encuentre
     * Actualiza los registros del envio marcando la hora en el que se realiza la entrada o salida
     * Guardando tambien si es una u otra.
     * @param envio Envio del cual se va a registrar una entrada o salida
     */
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

    /**
     * Actualiza los estados de los envios si cumplen las condiciones
     * @param envio Envio del cual se va a actualizar el estado si cumple con las condiciones
     */

    private void actualizarEstadoEnvio(Envio envio) {
        //Actualizar Estado
        if (envio.getRegistroActual() == envio.getRuta().size() - 2) {
            envio.setEstado(Estado.EnReparto);

        }
        if (envio.getRegistroActual() == envio.getRuta().size() - 1) {
            envio.setEstado(Estado.Entregado);

        }
    }

    /**
     * Funcion funciona solo a las 00:00:00, inspecciona a los pedidos en transito
     * y si han pasado mas de 7 dias modifica su estado a Extraviado y los añade a otro mapa
     */
    public void actualizarEnviosExtraviados(LocalDateTime ahora){
        /*Ahora mismo la dejamos publica para usarla en los Tests
         y el pasarle un Localdatetime tambien es por esta razon, por definicion seria simplemente llamar al .now()*/
        if(ahora.getHour()==0 && ahora.getMinute()==0 && ahora.getSecond()==0){
            for (Envio envio : envios.values()) {
                if (envio.getEstado() == Estado.EnTransito && envio.getRegistroActual()!=0) {
                    LocalDateTime ultimoRegistro = (envio.getRuta().get(envio.getRegistroActual()-1)).getFecha();
                    long dias=ChronoUnit.DAYS.between(ultimoRegistro,ahora);
                    if(dias>7){
                        envio.setEstado(Estado.Extraviado);
                        enviosExtraviados.put(envio.getId(),envio);

                    }
                }
            }
        }
    }

    /**
     * Busca los envios extraviados en un intervalo de tiempo
     * @param desde Fecha desde donde se quiere buscar
     * @param hasta Fecha hasta donde se quiere buscar
     * @return Lista de envios extraviados dentro del intervalo de tiempo
     */
    public List<Envio> consultarEnviosExtraviados(LocalDateTime desde,LocalDateTime hasta){
        List<Envio> extraviados = new ArrayList<>();
        for (Envio envio: enviosExtraviados.values()
             ) {
            LocalDateTime ultimoRegistro=envio.getRuta().get(envio.getRegistroActual()-1).getFecha();
            if(ultimoRegistro.isAfter(desde) && ultimoRegistro.isBefore(hasta) ){
                extraviados.add(envio);


            }

        }


        return extraviados;
    }

    /**
     * @return Lista de todos los envios extraviados
     */
    public List<Envio> consultarEnviosExtraviados(){
        return new ArrayList<>(enviosExtraviados.values());

    }

    /**
     * Calcula el porcentaje de envios extraviados en el ultimo periodo de tiempo seleccionado
     * @param ultimo Opcion seleccionada por el usuario dia/mes/año
     * @return Porcentaje de envios extraviados
     */
    public double porcentajeEnviosExtraviados(String ultimo){
        //El Switch es pensando en un desplegable de opciones limitadas
        double porcentaje=0;
        List<Envio> extraviados;
        LocalDateTime ahora = LocalDateTime.now();
        switch (ultimo){

            case "dia":
                porcentaje=((double) consultarEnviosExtraviados(ahora.minus(1,ChronoUnit.DAYS),ahora).size()/envios.values().size())*100;
                break;

            case "mes":
                porcentaje=((double) consultarEnviosExtraviados(ahora.minus(1,ChronoUnit.MONTHS),ahora).size()/envios.values().size())*100;
                break;

            case "anio":
                extraviados= consultarEnviosExtraviados(ahora.minus(1,ChronoUnit.YEARS),ahora);
                porcentaje=((double) extraviados.size()/envios.values().size())*100;
                break;

        }

        return porcentaje;
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

    /**
     * Activa la notificacion en un envio
     * @param idenvio ID del envio
     * @param noti Punto donde se quiere tener una notificacion de su llegada/salida
     */
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

    /**
     * Devuelve la situacion actual del envio
     * @param idenvio ID del envio
     * @return Cadena de texto con la informacion
     */
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

    /**
     * Devuelve toda la informacion sobre la ruta de un pedido(hasta el momento o ya finalizado)
     * @param idenvio ID del envio
     * @return Cadena de texto con la informacion
     */
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
