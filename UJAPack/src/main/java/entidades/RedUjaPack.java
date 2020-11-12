package entidades;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.Oficina;
import entidades.PuntoRuta.PuntoRuta;
import excepciones.DireccionesIncorrectas;

import javax.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class RedUjaPack {


    /*Datos del fichero json*/
    static Map<Integer, PuntoRuta> puntos;

    public RedUjaPack() {
        puntos = new HashMap<>();
        try {
            leerJson("src\\main\\resources\\redujapack.json");
        } catch (IOException ex) {
           // throw new LeerJsonIncorrecto(); Provoca fallo en crear el Bean
        }

    }
    /**
     * Lee el Json de Puntos de Ruta
     * @param file el nombre del archivo
     */

    private static void leerJson(String file) throws IOException {

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

            puntos.put(id, centroNodo);
            JsonArray provincias = centroJson.getAsJsonArray("provincias");

            for (JsonElement provincia : provincias) {
                Oficina oficinaNodo = new Oficina(cont++, nombre, localizacion, provincia.getAsString());
                oficinaNodo.setConexion(centroNodo);
                centroNodo.setConexion(oficinaNodo);
                puntos.put(oficinaNodo.getId(), oficinaNodo);

            }

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
                puntos.get(centro).setConexion(puntos.get(con));

            }

        }

    }

    /**
     * Busqueda Recursiva de caminos hacia una solucion
     * @param actual Punto de Ruta actual en la busqueda
     * @param destino Punto de Ruta final que deseamos alcanzar
     * @param camino Set de ID's de Puntos de Ruta, se asegura no repetir punto en el proceso de crear un camino
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
     * @param camino Set de ID's de Puntos de Ruta, se asegura no repetir punto en el proceso de crear un camino
     * @return La lista de Puntos de Ruta
     */
    private List<PuntoRuta> convertirSetEnLista(LinkedHashSet<Integer> camino) {
        List<PuntoRuta> lista = new ArrayList<>();
        for (Integer id : camino) {
            lista.add(puntos.get(id));
        }
        return lista;
    }

    /**
     * Calculo de la ruta Minima desde un punto de ruta a otro
     * @param origen Punto de Ruta desde el cual inicia  la busqueda
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
     * @param lugar Nombre del punto de ruta
     * @return ID del punto de ruta
     */
    private int convertirStringEnPuntoRuta(String lugar) {

        for (PuntoRuta value : puntos.values()) {
            if (value.getLugar().equals(lugar)) {
                return value.getId();
            }
        }
        throw new DireccionesIncorrectas();
    }
    /**
     * Dado dos Strings, uno del punto de incio del envio
     * y otro del lugar al que se desea mandar devuelve la ruta minima de Puntos de Ruta
     * @param remitente Nombre del punto de ruta de Inicio
     * @param destinatario Nombre del punto de ruta de Finalizacion
     * @return Lista de Puntos de Ruta, el camino minimo
     */
    public List<PuntoRuta> listaRutaMinima(@NotBlank String remitente, @NotBlank String destinatario) {
        return calcularRuta(puntos.get(convertirStringEnPuntoRuta(remitente)), puntos.get(convertirStringEnPuntoRuta(destinatario)));

    }
}
