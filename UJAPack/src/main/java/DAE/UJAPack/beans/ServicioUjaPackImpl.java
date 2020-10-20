/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAE.UJAPack.beans;

import DAE.UJAPack.entidades.Envio;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Component;



@Component
public class ServicioUjaPackImpl {
    
    /**
     * Mapa para guardar los envios usando el identificador como clave
     */
    private Map<String, Envio> envios;
    
    private Random rand = new Random();
    
    public ServicioUjaPackImpl() {
        
        envios = new HashMap<>();
        
    }
    
}
