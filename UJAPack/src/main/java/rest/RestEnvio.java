package rest;

import DTO.DTOEnvio;
import beans.UjaPack;
import entidades.Envio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestEnvio.URI_MAPPING)
public class RestEnvio {// CAMBIAR VERBOS, CREAR UN SOLO RESTSERVICE EN BEANS
    public static final String URI_MAPPING ="/envio";

    @Autowired
    private UjaPack ujaPack;

    @GetMapping("/test")
    public ResponseEntity comprobar() {
        return ResponseEntity.ok("API REST funciona correctamente (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
    }

    @GetMapping(value = "/{id}/ruta")
    public List<String> consultarRutaEnvio (@PathVariable("id") long id){
        return ujaPack.listadoRutaEnvio(id);

    }

    //@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @GetMapping(value = "/{id}")
    public DTOEnvio consultarEnvio (@PathVariable("id") long id){
        Envio envi= ujaPack.verEnvio(id);
        DTOEnvio dto = new DTOEnvio(envi);

        dto.setRuta(ujaPack.listadoRutaEnvio(id));


        return dto;
    }

    @GetMapping(value = "/{id}/actual")
    public ResponseEntity consultarEstadoEnvio (@PathVariable("id") long id){

        return ResponseEntity.ok(ujaPack.situacionActualEnvio(id));
    }

    @RequestMapping(value = "/nuevoenvio", method = RequestMethod.POST, headers = {"content-type=application/json"})
   // @PostMapping("/nuevoenvio")
    public ResponseEntity registrarEnvio(@RequestBody DTOEnvio envio){
        ujaPack.generarEnvio(envio.getRemitente(),envio.getDestinatario(),envio.getPeso(), envio.getDimensiones(),envio.getDatos_remitente() , envio.getDatos_destinatario());
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("/siguientepunto")
    public ResponseEntity avanzarEnvios(){
        ujaPack.avanzarEnvios();
        return ResponseEntity.ok().build();

    }

    @PutMapping("/{id}/siguientepunto")
    public ResponseEntity avanzarEnvioID(@PathVariable("id") long id){
        ujaPack.avanzarEnvioID(id);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/{id}/nuevanotificacion")
    public ResponseEntity nuevaNotificacionEnvio(@PathVariable("id") long id,@RequestBody String notifi){
        ujaPack.activarNotificacion(id,notifi);
        return ResponseEntity.ok().build();

    }



}
