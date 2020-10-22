package entidades;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.*;
import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.Oficina;
import entidades.PuntoRuta.PuntoRuta;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RedUjaPack {

    /*Datos del fichero json*/
    static Map<Integer, PuntoRuta> puntos;

    public RedUjaPack (){
        puntos = new HashMap<>();


    }

    public static void leerJson(String file) throws IOException {

        Map<Integer, ArrayList<Integer>> conexiones = new HashMap<>();
        int contadirPro = 20;

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        StringBuilder strB = new StringBuilder();
        String strAux = null;

        while((strAux = br.readLine()) != null){
            strB.append(strAux);

        }

        String jsonStr = strB.toString();
        JsonObject raiz = new Gson().fromJson(jsonStr, JsonObject.class);


        Set<String> centrosLogStr = raiz.getAsJsonObject().keySet();

        for(String centroStr : centrosLogStr){
            JsonObject centroJson = raiz.getAsJsonObject(centroStr);

            int id = Integer.parseInt(centroStr);
            String nombre = centroJson.get("nombre").toString();
            String localizacion = centroJson.get("localización").toString();
            CentroLog centroNodo = new CentroLog(id,nombre,localizacion);
            puntos.put(id,centroNodo);

            JsonArray provincias = centroJson.getAsJsonArray("provincias");

            for( JsonElement provincia : provincias){
                Oficina oficinaNodo = new Oficina(contadirPro++,nombre,localizacion, provincia.getAsString());
                oficinaNodo.setConexion(centroNodo);
                centroNodo.setConexion(oficinaNodo);
                puntos.put(oficinaNodo.getId(),oficinaNodo);

            }

            JsonArray conexionesAux = centroJson.getAsJsonArray("conexiones");
            ArrayList<Integer> conexTmp = new ArrayList<>();

            for(JsonElement conexion : conexionesAux){
                conexTmp.add(Integer.parseInt(conexion.getAsString()));

            }

            conexiones.put(id,conexTmp);


        }

        Set<Integer> centrosLogisticosSet =conexiones.keySet();

        for(Integer centro :centrosLogisticosSet){
            for (Integer con : conexiones.get(centro)){
                puntos.get(centro).setConexion(puntos.get(con));


            }


        }

        System.out.println(puntos.values());

    }


}
