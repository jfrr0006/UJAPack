package entidades;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.Oficina;
import entidades.PuntoRuta.PuntoRuta;
import excepciones.DireccionesIncorrectas;
import excepciones.LeerJsonIncorrecto;

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
            throw new LeerJsonIncorrecto();
        }

    }

    private static void leerJson(String file) throws IOException {

        Map<Integer, ArrayList<Integer>> conexiones = new HashMap<>();
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

    private List<PuntoRuta> convertirSetEnLista(LinkedHashSet<Integer> camino) {
        List<PuntoRuta> lista = new ArrayList<>();
        for (Integer id : camino) {
            lista.add(puntos.get(id));
        }
        return lista;
    }


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

    private int convertirStringEnPuntoRuta(String lugar) {

        for (PuntoRuta value : puntos.values()) {
            if (value.getLugar().equals(lugar)) {
                return value.getId();
            }
        }
        throw new DireccionesIncorrectas();
    }

    public List<PuntoRuta> listaRutaMinima(@NotBlank String remitente, @NotBlank String destinatario) {//Se Borrara o modificará
        int orig = convertirStringEnPuntoRuta(remitente);
        int dest = convertirStringEnPuntoRuta(destinatario);
        return calcularRuta(puntos.get(orig), puntos.get(dest));

    }
}
