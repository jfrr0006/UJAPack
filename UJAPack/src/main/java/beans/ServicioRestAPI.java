package beans;

import DTO.EnvioDTO;
import com.google.gson.Gson;
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

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping(ServicioRestAPI.URI_MAPPING)
public class ServicioRestAPI {
    public static final String URI_MAPPING = "/ujapack";
    private static final Gson gson = new Gson();
    @Autowired
    Mapeador mapper;
    @Autowired
    private UjaPack ujaPack;

    @ExceptionHandler(TransactionSystemException.class) // Con el @ExceptionHandler(ConstraintViolationException.class) directamente no nos funcionaba
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerViolacionRestricciones(TransactionSystemException e) {
        Throwable cause = e.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) cause).getConstraintViolations();
            String message = "";
            for (Object o :
                    constraintViolations.toArray()) {
                message += o.toString() + "  ";

            }

            return ResponseEntity.badRequest().body(gson.toJson(message));
        }
        return ResponseEntity.badRequest().body(gson.toJson(e.getMessage()));

    }

    @ExceptionHandler(EnvioNoRegistrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerEnvioNoRegistrado(EnvioNoRegistrado e) {
    }

    @ExceptionHandler({DireccionesIncorrectas.class, DirNotificacionIncorrecta.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerDireccionesIncorrectas(RuntimeException e) {
        return ResponseEntity.badRequest().body(gson.toJson("Direccion incorrecta o no permitida"));
    }

    @ExceptionHandler(OpcionPorcentaje.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerOpcionPorcentaje(OpcionPorcentaje e) {
        return ResponseEntity.badRequest().body(gson.toJson("Opcion consulta de porcentaje no soportada"));
    }

    @GetMapping("/")
    public ResponseEntity test() {
        return new ResponseEntity<>(gson.toJson("Funciona correctamente (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧"), HttpStatus.OK);
    }

    @GetMapping(value = "/envios/public/{id}")
    public ResponseEntity<EnvioDTO> consultaEnvio(@PathVariable("id") long id) {
        if (Long.toString(id).length() < 10) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Envio envi = ujaPack.verEnvio(id);
        return new ResponseEntity<>(mapper.aEnvioDTO(envi), HttpStatus.OK);

    }

    @GetMapping(value = "/envios/public/{id}/actual")
    public ResponseEntity<String> consultaEstadoEnvio(@PathVariable("id") long id) {
        if (Long.toString(id).length() < 10) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String actu = ujaPack.situacionActualEnvio(id);
        if (actu.equals("")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
        return new ResponseEntity<>(gson.toJson(actu), HttpStatus.OK);

    }

    @GetMapping(value = "/envios/public/{id}/ruta")
    public ResponseEntity<List<String>> detalleRutaEnvio(@PathVariable("id") long id) {
        if (Long.toString(id).length() < 10) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<String> lista = ujaPack.listadoRutaEnvio(id);

        if (lista == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
        return new ResponseEntity<>(lista, HttpStatus.OK);

    }

    @GetMapping(value = "/envios/private/extraviados")
    @CrossOrigin(origins = "*", maxAge = 10000, methods = RequestMethod.GET)
    public ResponseEntity<List<EnvioDTO>> consultarExtraviados(@RequestParam(required = false) String desdeFecha,
                                                               @RequestParam(required = false) String hastaFecha) {
        LocalDateTime fechaInicial;
        LocalDateTime fechaFinal;

        try {
            fechaInicial = desdeFecha != null ? LocalDateTime.parse(desdeFecha) : null;
            fechaFinal = hastaFecha != null ? LocalDateTime.parse(hastaFecha) : null;
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        List<Envio> lista;
        if (fechaInicial != null && fechaFinal != null) {
            lista = ujaPack.consultarEnviosExtraviados(fechaInicial, fechaFinal);
        } else {
            lista = ujaPack.consultarEnviosExtraviados();
        }
        List<EnvioDTO> listaDTO = new ArrayList<>();

        for (Envio envi :
                lista) {
            listaDTO.add(mapper.aEnvioDTO(envi));
        }

        return new ResponseEntity<>(listaDTO, HttpStatus.OK);


    }

    @GetMapping(value = "/envios/private/extraviados/porcentaje")
    public ResponseEntity<Double> consultarExtraviados(@RequestParam() String ultimo) {
        double porcentaje = ujaPack.porcentajeEnviosExtraviados(ultimo);

        return new ResponseEntity<>(porcentaje, HttpStatus.OK);


    }

    @PostMapping("/envios/private/nuevoenvio")
    public ResponseEntity<EnvioDTO> nuevoEnvio(@RequestBody EnvioDTO envio) {
        Envio envi = ujaPack.generarEnvio(envio.getRemitente(), envio.getDestinatario(), envio.getPeso(), envio.getDimensiones(), envio.getDatos_remitente(), envio.getDatos_destinatario());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aEnvioDTO(envi));

    }

    @PutMapping("/envios/private/siguientepunto")
    public ResponseEntity avanzarEnvios() {
        ujaPack.avanzarEnvios();
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @PutMapping("/envios/private/{id}/siguientepunto")
    public ResponseEntity avanzarEnvioID(@PathVariable("id") long id) {
        if (Long.toString(id).length() < 10) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ujaPack.avanzarEnvioID(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }


    @PutMapping("/envios/private/testextraviados") //Funcion para poder probar actualizar desde los test del rest
    public ResponseEntity actualizarEnviosExtraviados() {
        ujaPack.actualizarEnviosExtraviadosTest();
        return ResponseEntity.status(HttpStatus.OK).build();

    }



/*
    @PutMapping("/envios/public/{id}/nuevanotificacion")
    public ResponseEntity nuevaNotificacionEnvio(@PathVariable("id") long id, @RequestParam() String notifi) {
        ujaPack.activarNotificacion(id, notifi);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

 */

}
