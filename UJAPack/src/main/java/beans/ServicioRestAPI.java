package beans;

import DTO.EnvioDTO;
import entidades.Envio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ServicioRestAPI.URI_MAPPING)
public class ServicioRestAPI {
    public static final String URI_MAPPING ="/ujapack";

    @Autowired
    private UjaPack ujaPack;

    @Autowired
    Mapeador mapper;

    @GetMapping("/test")
    public ResponseEntity test() {
        return new ResponseEntity<>("API REST funciona correctamente (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧",HttpStatus.OK);
    }

    @GetMapping(value = "/envios/{id}/ruta")
    public ResponseEntity<List<String>> detalleRutaEnvio (@PathVariable("id") long id){
        if (id <= 0 ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Envio envi= ujaPack.verEnvio(id);

        if(envi == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        List<String> lista =ujaPack.listadoRutaEnvio(id);

        if(lista == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }

        return new ResponseEntity<>(lista, HttpStatus.OK);

    }

    //@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @GetMapping(value = "/envios/{id}")
    public ResponseEntity<EnvioDTO> consultaEnvio (@PathVariable("id") long id){
        if (Long.toString(id).length() < 10 ) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Envio envi= ujaPack.verEnvio(id);

        if(envi == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<>(mapper.aEnvioDTO(envi), HttpStatus.OK);
    }

    @GetMapping(value = "/envios/{id}/actual")
    public ResponseEntity<String> consultaEstadoEnvio (@PathVariable("id") long id){
        Envio envi= ujaPack.verEnvio(id);
        if(envi == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        String actu =ujaPack.situacionActualEnvio(id);
        if(actu == null || actu ==""){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
        return new ResponseEntity<>(actu,HttpStatus.OK);
    }

    @RequestMapping(value = "/envios/nuevoenvio", method = RequestMethod.POST, headers = {"content-type=application/json"})
   // @PostMapping("/nuevoenvio")
    public ResponseEntity nuevoEnvio(@RequestBody EnvioDTO envio){
        ujaPack.generarEnvio(envio.getRemitente(),envio.getDestinatario(),envio.getPeso(), envio.getDimensiones(),envio.getDatos_remitente() , envio.getDatos_destinatario());
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("/envios/siguientepunto")
    public ResponseEntity avanzarEnvios(){
        ujaPack.avanzarEnvios();
        return ResponseEntity.ok().build();

    }

    @PutMapping("/envios/{id}/siguientepunto")
    public ResponseEntity avanzarEnvioID(@PathVariable("id") long id){
        ujaPack.avanzarEnvioID(id);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/envios/{id}/nuevanotificacion")
    public ResponseEntity nuevaNotificacionEnvio(@PathVariable("id") long id,@RequestBody String notifi){
        ujaPack.activarNotificacion(id,notifi);
        return ResponseEntity.ok().build();

    }



}
