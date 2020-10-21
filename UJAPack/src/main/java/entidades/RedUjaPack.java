package entidades;

import com.google.gson.Gson;
import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.Oficina;
import entidades.PuntoRuta.PuntoRuta;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedUjaPack {

    /*Datos del fichero json*/
    Map<Integer, PuntoRuta> puntos;

    public RedUjaPack (){
        this.puntos = new HashMap<>();


    }

    private static void leerJson(String file) throws IOException {

        Map<Integer, ArrayList<Integer>> conexiones = new HashMap<>();
        int contadirPro = 0;

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        StringBuilder strB = new StringBuilder();
        String strAux = null;

        while((strAux = br.readLine()) != null){
            strB.append(strAux);

        }

        String jsonStr = strB.toString();
        JsonObject raiz = new Gson().fromJson(jsonStr, JsonObject.class);

        Set<String> centrosLogStr = raiz.keySet();

        for(String centroStr : centrosLogStr){
            JsonObject centroJson = raiz.getJsonObject(centroStr);

            int id = Integer.parseInt(centroStr);
            String nombre = centroJson.get("nombre").toString();
            String localizacion = centroJson.get("localización").toString();
            CentroLog centroNodo = new CentroLog(id,nombre,localizacion);

            JsonArray provincias = centroJson.getJsonArray("provincias");

            for( JsonValue provincia : provincias){
                Oficina oficinaNodo = new Oficina(contadirPro++,nombre,localizacion, provincia.toString());
                oficinaNodo.setConexion(centroNodo);


            }


        }


    }


}
