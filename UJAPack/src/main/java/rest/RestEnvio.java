package rest;

import DTO.DTOEnvio;
import beans.UjaPack;
import entidades.Envio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(RestEnvio.URI_MAPPING)
public class RestEnvio {
    public static final String URI_MAPPING ="/envio";

    @Autowired
    private UjaPack ujaPack;

    @GetMapping("/test")
    public ResponseEntity comprobar() {
        return ResponseEntity.ok("API funciona correctamente (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
    }

    @GetMapping(value = "/{id}/ruta")
    public ResponseEntity consultarRutaEnvio (@PathVariable("id") long id){
        return ResponseEntity.ok(ujaPack.listadoRutaEnvio(id));

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity consultarEnvio (@PathVariable("id") long id){
        Envio envi= ujaPack.verEnvio(id);
        List<String> listado= new ArrayList<>();
        listado.add(String.valueOf(envi.getId()));
        listado.add(String.valueOf(envi.getImporte()));


        return ResponseEntity.ok(listado);
    }

    @GetMapping(value = "/{id}/actual")
    public ResponseEntity consultarEstadoEnvio (@PathVariable("id") long id){

        return ResponseEntity.ok(ujaPack.situacionActualEnvio(id));
    }

    @RequestMapping(value = "/nuevoenvio", method = RequestMethod.POST, headers = {"content-type=application/json"})
   // @PostMapping("/nuevoenvio")
    public void registrarEnvio(@RequestBody DTOEnvio envio){
        ujaPack.generarEnvio(envio.getRemitente(),envio.getDestinatario(),envio.getPeso(), envio.getDimensiones(),envio.getDatos_remitente() , envio.getDatos_destinatario());



    }






}
