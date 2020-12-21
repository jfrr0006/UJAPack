package beans;

import DTO.EnvioDTO;
import entidades.Envio;
import excepciones.DirNotificacionIncorrecta;
import excepciones.DireccionesIncorrectas;
import excepciones.EnvioNoRegistrado;
import excepciones.OpcionPorcentaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(ServicioRestAPI.URI_MAPPING)
public class ServicioRestAPI {
    public static final String URI_MAPPING ="/ujapack";

    @Autowired
    private UjaPack ujaPack;

    @Autowired
    Mapeador mapper;

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerViolacionRestricciones(TransactionSystemException e) {
        Throwable cause = e.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) cause).getConstraintViolations();
            String message="";
            for (Object o:
            constraintViolations.toArray()) {
                message+=o.toString()+"  ";

            }
            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler(EnvioNoRegistrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerEnvioNoRegistrado(EnvioNoRegistrado e) {
    }

    @ExceptionHandler({DireccionesIncorrectas.class,DirNotificacionIncorrecta.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerDireccionesIncorrectas(DireccionesIncorrectas e) {
        return ResponseEntity.badRequest().body("Direccion incorrecta o no permitida");
    }

    @ExceptionHandler(OpcionPorcentaje.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerOpcionPorcentaje(OpcionPorcentaje e) {
        return ResponseEntity.badRequest().body("Opcion consulta de porcentaje no soportada");
    }

    @GetMapping("/")
    public ResponseEntity test() {
        return new ResponseEntity<>("Funciona correctamente (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧",HttpStatus.OK);
    }

    @GetMapping(value = "/envios/{id}")
    public ResponseEntity<EnvioDTO> consultaEnvio (@PathVariable("id") long id){
        if (Long.toString(id).length() < 10 ) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Envio envi= ujaPack.verEnvio(id);
        return new ResponseEntity<>(mapper.aEnvioDTO(envi), HttpStatus.OK);

    }

    @GetMapping(value = "/envios/{id}/actual")
    public ResponseEntity<String> consultaEstadoEnvio (@PathVariable("id") long id){
        String actu =ujaPack.situacionActualEnvio(id);
        if(actu.equals("")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
        return new ResponseEntity<>(actu,HttpStatus.OK);

    }

    @GetMapping(value = "/envios/{id}/ruta")
    public ResponseEntity<List<String>> detalleRutaEnvio (@PathVariable("id") long id){
        if (Long.toString(id).length() < 10 ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<String> lista =ujaPack.listadoRutaEnvio(id);

        if(lista == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
        return new ResponseEntity<>(lista, HttpStatus.OK);

    }

    @GetMapping(value = "/envios/extraviados")
    public ResponseEntity<List<EnvioDTO>> consultarExtraviados (@RequestParam(required=false) String desdeFecha,
                                                              @RequestParam(required=false) String hastaFecha){
        LocalDateTime fechaInicial;
        LocalDateTime fechaFinal;

        try {
            fechaInicial = desdeFecha != null ? LocalDateTime.parse(desdeFecha) : null;
            fechaFinal = hastaFecha != null ? LocalDateTime.parse(hastaFecha) : null;
        }
        catch(DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        List<Envio> lista;
        if(fechaInicial != null && fechaFinal != null){
            lista = ujaPack.consultarEnviosExtraviados(fechaInicial,fechaFinal);
        }else{
            lista = ujaPack.consultarEnviosExtraviados();
        }
        List<EnvioDTO> listaDTO = new ArrayList<>();

        for (Envio envi:
                lista) {
            listaDTO.add(mapper.aEnvioDTO(envi));
        }

        return new ResponseEntity<>(listaDTO, HttpStatus.OK);


    }
    @GetMapping(value = "/envios/extraviados/porcentaje")
    public ResponseEntity<String> consultarExtraviados (@RequestParam() String ultimo){
        double porcentaje= ujaPack.porcentajeEnviosExtraviados(ultimo);

        return new ResponseEntity<>("El porcentaje de envios extraviados en el ultimo "+ultimo+" es de "+Double.toString(porcentaje), HttpStatus.OK);


    }

    @PostMapping("/envios/nuevoenvio")
    public ResponseEntity nuevoEnvio(@RequestBody EnvioDTO envio){
        Envio envi= ujaPack.generarEnvio(envio.getRemitente(),envio.getDestinatario(),envio.getPeso(), envio.getDimensiones(),envio.getDatos_remitente() , envio.getDatos_destinatario());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aEnvioDTO(envi));

    }

    @PutMapping("/envios/siguientepunto")
    public ResponseEntity avanzarEnvios(){
        ujaPack.avanzarEnvios();
        return ResponseEntity.status(HttpStatus.CREATED).body("Todos los envios se han avanzado correctamente");

    }

    @PutMapping("/envios/{id}/siguientepunto")
    public ResponseEntity avanzarEnvioID(@PathVariable("id") long id){
        ujaPack.avanzarEnvioID(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aEnvioDTO(ujaPack.verEnvio(id)));

    }

    @PutMapping("/envios/{id}/nuevanotificacion")
    public ResponseEntity nuevaNotificacionEnvio(@PathVariable("id") long id,@RequestBody String notifi){
        ujaPack.activarNotificacion(id,notifi);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aEnvioDTO(ujaPack.verEnvio(id)));

    }



}
