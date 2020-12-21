package beans;

import Utils.Estado;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entidades.Envio;
import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.Oficina;
import entidades.PuntoRuta.PuntoRuta;
import entidades.Registro;
import excepciones.DirNotificacionIncorrecta;
import excepciones.DireccionesIncorrectas;
import excepciones.EnvioNoRegistrado;
import excepciones.OpcionPorcentaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import servicios.ServicioUjaPack;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class UjaPack implements ServicioUjaPack {

    @Autowired
    private RepositorioEnvio repoEnvios;

    @Autowired
    private RepositorioPuntoRuta repoPuntosRuta;

    @Autowired
    private RepositorioRegistro repoRegistro;


    public UjaPack() {


    }

    /**
     * Genera un nuevo envio
     *
     * @param remitente           Nombre del lugar de Inicio del envio
     * @param destinatario        Nombre del lugar de Finalizacion del envio
     * @param _datos_remitente    Datos de la persona que realiza el envio
     * @param _datos_destinatario Datos de la persona que recibe el envio
     * @param peso                Peso del paquete
     * @param dimensiones         Dimensiones del paquete
     * @return Nuevo envio creado
     */
    @Override
    public Envio generarEnvio(@NotBlank String remitente, @NotBlank String destinatario, @Positive Float peso, @Positive Float dimensiones, @NotBlank String _datos_remitente, @NotBlank String _datos_destinatario) {

        List<PuntoRuta> ruta = listaRutaMinima(remitente, destinatario);
        List<Registro> registros = new ArrayList<>();

        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        Envio nuevoEnvio = new Envio(number, calcularCosto(ruta.size(), peso, dimensiones), registros, peso, dimensiones, remitente, destinatario, _datos_remitente, _datos_destinatario);
        repoEnvios.insertar(nuevoEnvio);

        for (PuntoRuta punto : ruta) {
            Registro aux1 = new Registro(punto);
            Registro aux2 = new Registro(punto);

            repoRegistro.insertar(aux1);
            repoRegistro.insertar(aux2);
            nuevoEnvio.getRuta().add(aux1);
            nuevoEnvio.getRuta().add(aux2);


        }
        Registro aux3 = new Registro(ruta.get(ruta.size() - 1));
        repoRegistro.insertar(aux3);
        nuevoEnvio.getRuta().add(aux3);//Metemos el ultimo de nuevo por 3º vez para el registro entregado

        repoEnvios.actualizar(nuevoEnvio);

        return nuevoEnvio;
    }

    private Float calcularCosto(int numPuntosRuta, Float peso, Float dimensiones) {
        return (peso * dimensiones * (numPuntosRuta + 1)) / 1000;

    }

    /**
     * Avanza todos los envios que no esten ya en estado de Entregado
     */
    @Override
    @Transactional
    public void avanzarEnvios() {
        for (Envio envio : repoEnvios.listEnvios()) {
            if (envio.getEstado() != Estado.Entregado) {
                registroES(envio);
                actualizarEstadoEnvio(envio);
            }

        }
    }

    /**
     * Avanza un envio en concreto
     */
    @Override
    @Transactional
    public void avanzarEnvioID(long idenvio) {
        Envio envio = repoEnvios.buscar(idenvio).orElseThrow(EnvioNoRegistrado::new);
        if (envio.getEstado() != Estado.Entregado) {
                registroES(envio);
                actualizarEstadoEnvio(envio);
            }


    }

    @Override
    @Transactional
    public Envio verEnvio(long id) {
        Envio envio = repoEnvios.buscar(id).orElseThrow(EnvioNoRegistrado::new);
        envio.getRuta().size();
        return envio;
    }

    /**
     * Actualiza el estado actual del envio dependiendo de en que punto de la ruta se encuentre
     * Actualiza los registros del envio marcando la hora en el que se realiza la entrada o salida
     * Guardando tambien si es una u otra.
     *
     * @param envio Envio del cual se va a registrar una entrada o salida
     */
    private void registroES(Envio envio) {
        boolean entrada;
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
     *
     * @param envio Envio del cual se va a actualizar el estado si cumple con las condiciones
     */

    private void actualizarEstadoEnvio(Envio envio) {
        //Actualizar Estado
        if (envio.getRegistroActual() == envio.getRuta().size() - 1) {
            envio.setEstado(Estado.EnReparto);

        }
        if (envio.getRegistroActual() == envio.getRuta().size()) {
            envio.setEstado(Estado.Entregado);

        }
    }

    /**
     * Funcion funciona solo a las 00:00:00, inspecciona a los pedidos en transito
     * y si han pasado mas de 7 dias modifica su estado a Extraviado y los añade a otro mapa
     */
    @Override
    public void actualizarEnviosExtraviados(@NotNull LocalDateTime ahora) { //Funcion Para test
       /*Ahora mismo la dejamos publica para usarla en los Tests
        y el pasarle un Localdatetime tambien es por esta razon, por definicion seria simplemente llamar al .now()*/
        if (ahora.getHour() == 0 && ahora.getMinute() == 0 && ahora.getSecond() == 0) {
            for (Envio envio : repoEnvios.listEnvios()) {
                if (envio.getEstado() == Estado.EnTransito && envio.getRegistroActual() != 0) {
                    LocalDateTime ultimoRegistro = (repoEnvios.listRuta(envio.getId()).get(envio.getRegistroActual() - 1)).getFecha();
                    long dias = ChronoUnit.DAYS.between(ultimoRegistro, ahora);
                    if (dias > 7) {
                        envio.setEstado(Estado.Extraviado);
                        repoEnvios.actualizar(envio);

                    }
                }
            }
        }
    }
  // @Override
    @Scheduled(cron = "0 0 0 * * *") //Funcion para su uso real
    public void actualizarEnviosExtraviados() {
       /*Ahora mismo la dejamos publica para usarla en los Tests
        y el pasarle un Localdatetime tambien es por esta razon, por definicion seria simplemente llamar al .now()*/
        LocalDateTime ahora= LocalDateTime.now();
            for (Envio envio : repoEnvios.listEnvios()) {
                if (envio.getEstado() == Estado.EnTransito && envio.getRegistroActual() != 0) {
                    LocalDateTime ultimoRegistro = (repoEnvios.listRuta(envio.getId()).get(envio.getRegistroActual() - 1)).getFecha();
                    long dias = ChronoUnit.DAYS.between(ultimoRegistro, ahora);
                    if (dias > 7) {
                        envio.setEstado(Estado.Extraviado);
                        repoEnvios.actualizar(envio);

                    }
                }
            }
    }

    /**
     * Busca los envios extraviados en un intervalo de tiempo
     *
     * @param desde Fecha desde donde se quiere buscar
     * @param hasta Fecha hasta donde se quiere buscar
     * @return Lista de envios extraviados dentro del intervalo de tiempo
     */
    @Override
    public List<Envio> consultarEnviosExtraviados(LocalDateTime desde, LocalDateTime hasta) {
        List<Envio> extraviados = new ArrayList<>();
        for (Envio envio : repoEnvios.listEnviosExtraviados()) {
            int registroActual = envio.getRegistroActual() - 1;
                LocalDateTime ultimoRegistro = repoEnvios.listRuta(envio.getId()).get(registroActual).getFecha();
                if (ultimoRegistro.isAfter(desde) && ultimoRegistro.isBefore(hasta)) {
                    extraviados.add(envio);


                }

        }

        return extraviados;
    }

    /**
     * @return Lista de todos los envios extraviados
     */
    @Override
    public List<Envio> consultarEnviosExtraviados() {
        return new ArrayList<>(repoEnvios.listEnviosExtraviados());

    }

    /**
     * Calcula el porcentaje de envios extraviados en el ultimo periodo de tiempo seleccionado
     *
     * @param ultimo Opcion seleccionada por el usuario dia/mes/año
     * @return Porcentaje de envios extraviados
     */
    @Override
    public double porcentajeEnviosExtraviados(@NotBlank String ultimo) {
        //El Switch es pensando en un desplegable de opciones limitadas
        double porcentaje = 0;
        LocalDateTime ahora = LocalDateTime.now();
        switch (ultimo.toLowerCase()) {

            case "dia":
                porcentaje = ((double) consultarEnviosExtraviados(ahora.minus(1, ChronoUnit.DAYS), ahora).size() / repoEnvios.listEnvios().size()) * 100;
                break;

            case "mes":
                porcentaje = ((double) consultarEnviosExtraviados(ahora.minus(1, ChronoUnit.MONTHS), ahora).size() / repoEnvios.listEnvios().size()) * 100;
                break;

            case "anio":
                porcentaje = ((double) consultarEnviosExtraviados(ahora.minus(1, ChronoUnit.YEARS), ahora).size() / repoEnvios.listEnvios().size()) * 100;
                break;

            default:
                throw new OpcionPorcentaje();
        }

        return porcentaje;
    }

    private void mandarNotificacion(Envio envio) {
        //Antes sacabamos por pantalla pero hemos rectificado, ahora lo almacenamos con el objetivo de poder mostrarlo
        String es;
        if (envio.getRuta().get(envio.getRegistroActual()).getEntrada()) {
            es = "ha salido de ";
        } else {
            es = "ha entrado a ";
        }
        if (envio.getEstado() == Estado.EnReparto) {
            envio.setDatosNotificacion("El envio con identificador " + envio.getId() + " esta en " + envio.getNotificacion());

        } else {
            envio.setDatosNotificacion("El envio con identificador " + envio.getId() + " " + es + envio.getNotificacion());

        }

        repoEnvios.actualizar(envio);
    }

    /**
     * Activa la notificacion en un envio
     *
     * @param idenvio ID del envio
     * @param noti    Punto donde se quiere tener una notificacion de su llegada/salida
     */
    @Override
    public void activarNotificacion(long idenvio, @NotBlank String noti) {
        boolean existe = false;
        for (Registro regis : repoEnvios.listRuta(idenvio)) {
            if (noti.equals(regis.getPuntoR().getLugar()) && regis.isNull()) {
                existe = true;
                break;
            }

        }
        if (existe) {
            Envio envi = repoEnvios.buscar(idenvio).orElseThrow(EnvioNoRegistrado::new);
            envi.setNotificacion(noti);
            repoEnvios.actualizar(envi);
        } else {
            throw new DirNotificacionIncorrecta();
        }

    }

    /**
     * Devuelve la situacion actual del envio
     *
     * @param idenvio ID del envio
     * @return Cadena de texto con la informacion
     */
    @Override
    public String situacionActualEnvio(long idenvio) {
        String es;
        List<Registro> ruta = repoEnvios.listRuta(idenvio);
        Envio envi = repoEnvios.buscar(idenvio).orElseThrow(EnvioNoRegistrado::new);
        int registroActual = envi.getRegistroActual() - 1;

        if (envi.getEstado() == Estado.Entregado) {
            return "El envio ha llegado a su destino. Hora de entrega ->" + ruta.get(registroActual).getFecha().toString();
        }
        if (envi.getEstado() == Estado.EnReparto) {
            return "El envio empezó su reparto. Hora de inicio -> " + ruta.get(registroActual).getFecha().toString();
        }
        if (registroActual == -1) {
            return "El envio todavia no ha llegado al primer punto de control";
        } else {
            if (ruta.get(registroActual).getEntrada()) {
                es = "Ha entrado a ";
            } else {
                es = "Ha salido de ";
            }
        }
        return "Ubicacion -> " + es + ruta.get(registroActual).getPuntoR().getLugar() + " Hora -> " + ruta.get(registroActual).getFecha().toString();

    }

    /**
     * Devuelve toda la informacion sobre la ruta de un pedido(hasta el momento o ya finalizado)
     *
     * @param idenvio ID del envio
     * @return Cadena de texto con la informacion
     */
    @Override
    public List<String> listadoRutaEnvio(long idenvio) {
        String es;
        List<String> registros = new ArrayList<>();
        int cont = 0;
        int registroActual = repoEnvios.buscar(idenvio).orElseThrow(EnvioNoRegistrado::new).getRegistroActual() - 1;
        List<Registro> ruta = repoEnvios.listRuta(idenvio);

        if (registroActual == -1) {
            registros.add("El envio todavia no ha llegado al primer punto de control");
            return registros;
        }

        while (cont != ruta.size() && ruta.get(cont).getFecha() != null) {

            if (cont == ruta.size() - 1) {
                registros.add("El envio ha llegado a su destino. Hora de entrega -> " + ruta.get(cont).getFecha().toString());

            } else {

                if (cont == ruta.size() - 2) {
                    registros.add("El envio empezó su reparto. Hora de inicio -> " + ruta.get(cont).getFecha().toString());

                } else {
                    if (ruta.get(cont).getEntrada()) {
                        es = "Ha entrado a ";
                    } else {
                        es = "Ha salido de ";
                    }
                    registros.add("Ubicacion -> " + es + ruta.get(cont).getPuntoR().getLugar() + " Hora -> " + ruta.get(cont).getFecha().toString());
                }
            }
            cont++;
        }

        return registros;
    }

    /**
     * Lee el Json de Puntos de Ruta
     */
    @PostConstruct
    //@Transactional
    //  No funcionan ambas, se queda ignorado el transactional por lo que si queremos ahorrar codigo en los test seria asi,
    //  ya que al poner el PostConstruct una vez se crea con el Autowired se llama a esta funcion, funciona si la funcion no tiene argumentos por eso el String File esta dentro
    public void leerJson() throws IOException {
        String file = ".\\src\\main\\resources\\redujapack.json";
        if (repoPuntosRuta.listPuntosRuta().isEmpty()) {
            Map<Integer, ArrayList<Integer>> conexiones = new HashMap<>();
            /*Ponemos 11 para que en la estructura de datos, los centros esten desde la 0 al 10 y las oficinas empiecen en el 11
             * Pero en realidad si tuvieramos pensamiento de meter mas centros lo suyo seria que los indices de las oficinas empezaran
             * en un lugar mas avanzado EJ:100,200
             */
            int cont = 11;

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder strB = new StringBuilder();
            String strA;

            while ((strA = br.readLine()) != null) {
                strB.append(strA);

            }

            String jsonStr = strB.toString();
            JsonObject raiz = new Gson().fromJson(jsonStr, JsonObject.class);
            Set<String> centrosLogStr = raiz.getAsJsonObject().keySet();

            for (String centroStr : centrosLogStr) {
                JsonObject centroJson = raiz.getAsJsonObject(centroStr);

                int id = Integer.parseInt(centroStr);
                String nombre = centroJson.get("nombre").toString();
                String localizacion = centroJson.get("localización").toString();
                CentroLog centroNodo = new CentroLog(id, nombre, localizacion);

                repoPuntosRuta.insertar(centroNodo);
                JsonArray provincias = centroJson.getAsJsonArray("provincias");

                for (JsonElement provincia : provincias) {
                    Oficina oficinaNodo = new Oficina(cont++, nombre, localizacion, provincia.getAsString());
                    oficinaNodo.setConexion(centroNodo);
                    centroNodo.setConexion(oficinaNodo);
                    repoPuntosRuta.insertar(oficinaNodo);

                }
                repoPuntosRuta.actualizar(centroNodo);

                JsonArray conexionesAux = centroJson.getAsJsonArray("conexiones");
                ArrayList<Integer> conexTmp = new ArrayList<>();

                for (JsonElement conexion : conexionesAux) {
                    conexTmp.add(Integer.parseInt(conexion.getAsString()));

                }

                conexiones.put(id, conexTmp);


            }

            Set<Integer> centrosLogisticosSet = conexiones.keySet();

            for (Integer centro : centrosLogisticosSet) {
                for (Integer con : conexiones.get(centro)) {
                    PuntoRuta aux = repoPuntosRuta.buscar(centro).orElseThrow(EnvioNoRegistrado::new);
                    aux.setConexion(repoPuntosRuta.buscar(con).orElseThrow(EnvioNoRegistrado::new));
                    repoPuntosRuta.actualizar(aux);

                }

            }
        }
    }

    /**
     * Busqueda Recursiva de caminos hacia una solucion
     *
     * @param actual     Punto de Ruta actual en la busqueda
     * @param destino    Punto de Ruta final que deseamos alcanzar
     * @param camino     Set de ID's de Puntos de Ruta, se asegura no repetir punto en el proceso de crear un camino
     * @param soluciones La lista que contiene los caminos desde actual hasta destino encontrados.
     */
    private void busquedaRec(PuntoRuta actual, PuntoRuta destino, List<List<PuntoRuta>> soluciones, LinkedHashSet<Integer> camino) {

        camino.add(actual.getId());

        if (actual.getId() == destino.getId()) {
            soluciones.add(convertirSetEnLista(camino));

        } else {
            for (PuntoRuta conexion : actual.getConexiones().values()) {
                if (!camino.contains(conexion.getId())) {

                    busquedaRec(conexion, destino, soluciones, camino);
                }
            }
        }
        camino.remove(actual.getId());
    }

    /**
     * Transforma un set de enteros en una lista de Puntos de Ruta
     *
     * @param camino Set de ID's de Puntos de Ruta, se asegura no repetir punto en el proceso de crear un camino
     * @return La lista de Puntos de Ruta
     */
    private List<PuntoRuta> convertirSetEnLista(LinkedHashSet<Integer> camino) {
        List<PuntoRuta> lista = new ArrayList<>();
        for (Integer id : camino) {
            lista.add(repoPuntosRuta.buscar(id).orElseThrow(EnvioNoRegistrado::new));
        }
        return lista;
    }

    /**
     * Calculo de la ruta Minima desde un punto de ruta a otro
     *
     * @param origen  Punto de Ruta desde el cual inicia  la busqueda
     * @param destino Punto de Ruta final que deseamos alcanzar
     * @return El camino minimo encontrado
     */

    private List<PuntoRuta> calcularRuta(PuntoRuta origen, PuntoRuta destino) {

        List<List<PuntoRuta>> soluciones = new ArrayList<>();
        LinkedHashSet<Integer> camino = new LinkedHashSet<>();

        busquedaRec(origen, destino, soluciones, camino);

        int caminoMin = Integer.MAX_VALUE;
        List<PuntoRuta> solucionMin = null;

        for (List<PuntoRuta> solucion : soluciones) {

            if (solucion.size() < caminoMin) {
                caminoMin = solucion.size();
                solucionMin = solucion;

            }


        }
        return solucionMin;

    }

    /**
     * Dado un String busca este en los Puntos de Ruta y si existe devuelve el ID entero del punto
     *
     * @param lugar Nombre del punto de ruta
     * @return ID del punto de ruta
     */
    private int convertirStringEnPuntoRuta(String lugar) {

        for (PuntoRuta value : repoPuntosRuta.listPuntosRuta()) {
            if (value.getLugar().equals(lugar)) {
                return value.getId();
            }
        }
        throw new DireccionesIncorrectas();
    }

    /**
     * Dado dos Strings, uno del punto de incio del envio
     * y otro del lugar al que se desea mandar devuelve la ruta minima de Puntos de Ruta
     *
     * @param remitente    Nombre del punto de ruta de Inicio
     * @param destinatario Nombre del punto de ruta de Finalizacion
     * @return Lista de Puntos de Ruta, el camino minimo
     */
    @Override
    public List<PuntoRuta> listaRutaMinima(@NotBlank String remitente, @NotBlank String destinatario) {
        return calcularRuta(repoPuntosRuta.buscar(convertirStringEnPuntoRuta(remitente)).orElseThrow(EnvioNoRegistrado::new), repoPuntosRuta.buscar(convertirStringEnPuntoRuta(destinatario)).orElseThrow(EnvioNoRegistrado::new));

    }
}

