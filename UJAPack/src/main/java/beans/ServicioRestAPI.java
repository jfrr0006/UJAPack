package beans;

import DTO.EnvioDTO;
import com.google.gson.Gson;
import entidades.Envio;
import excepciones.DireccionesIncorrectas;
import excepciones.EnvioNoRegistrado;
import excepciones.OpcionPorcentaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Handler para excepciones de violación de restricciones
     * Si ponemos ConstraintViolationException.class no las captura, es decir, si tenemos @Positive y ponemos -10 en el peso
     * Salta una excepcion de ConstraintViolationException pero el ExceptionHandler no la captura y produce en vez de producir el error 400 produce el error 500
     * Entendemos que el Entity manager lanza una excepcion de rollback cuando algo va mal en la transaccion y esta se convierte en TransactionSystemException, o eso hemos leido
     * Seguramente sea alguna anotacion que se nos ha pasado y no encontramos, aunque sabemos que teoricamente lo correcto seria ConstraintViolationException.class,
     * vamos a dejarlo asi para que salgan los errores correctos
     */
    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handlerViolacionRestricciones(TransactionSystemException e) {
    }

    /**
     * Handler para excepciones de Envios no registrados
     */
    @ExceptionHandler(EnvioNoRegistrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerEnvioNoRegistrado(EnvioNoRegistrado e) {
    }

    /**
     * Handler para excepciones de Puntos de ruta no correctos
     */
    @ExceptionHandler(DireccionesIncorrectas.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handlerDireccionesIncorrectas(DireccionesIncorrectas e) {
    }

    /**
     * Handler para excepciones sobre las opciones de los porcentajes de los envios extraviados (dia mes y año si intenta pasarle un valore no permitido)
     */
    @ExceptionHandler(OpcionPorcentaje.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handlerOpcionPorcentaje(OpcionPorcentaje e) {
    }

    @GetMapping("/")
    public ResponseEntity test() {
        return new ResponseEntity<>(gson.toJson("Funciona correctamente (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧"), HttpStatus.OK);
    }

    @GetMapping(value = "/public/envios/{id}")
    public ResponseEntity<EnvioDTO> consultaEnvio(@PathVariable("id") long id) {
        if (Long.toString(id).length() < 10) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Envio envi = ujaPack.verEnvio(id);
        return new ResponseEntity<>(mapper.aEnvioDTO(envi), HttpStatus.OK);

    }

    @GetMapping(value = "/public/envios/{id}/actual")
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

    @GetMapping(value = "/public/envios/{id}/ruta")
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

    @GetMapping(value = "/private/envios/extraviados")
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

        for (Envio envi : lista) {
            listaDTO.add(mapper.aEnvioDTO(envi));
        }

        return new ResponseEntity<>(listaDTO, HttpStatus.OK);


    }

    @GetMapping(value = "/private/envios/extraviados/porcentaje")
    public ResponseEntity<Double> consultarExtraviados(@RequestParam() String ultimo) {
        double porcentaje = ujaPack.porcentajeEnviosExtraviados(ultimo);

        return new ResponseEntity<>(porcentaje, HttpStatus.OK);


    }

    @PostMapping("/private/envios/envio")
    public ResponseEntity<EnvioDTO> nuevoEnvio(@RequestBody EnvioDTO envio) {
        Envio envi = ujaPack.generarEnvio(envio.getRemitente(), envio.getDestinatario(), envio.getPeso(), envio.getDimensiones(), envio.getDatos_remitente(), envio.getDatos_destinatario());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aEnvioDTO(envi));

    }

    @PutMapping("/private/envios/")
    public ResponseEntity avanzarEnvios() {
        ujaPack.avanzarEnvios();
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @PutMapping("/private/envios/{id}/")
    public ResponseEntity avanzarEnvioID(@PathVariable("id") long id) {
        if (Long.toString(id).length() < 10) {//Numero menor de 10 cifras
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ujaPack.avanzarEnvioID(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }


    @PutMapping("/private/envios/testextraviados") //Funcion para poder probar actualizar desde los test del rest
    public ResponseEntity actualizarEnviosExtraviados() {
        ujaPack.actualizarEnviosExtraviadosTest();
        return ResponseEntity.status(HttpStatus.OK).build();

    }



/*
    @PutMapping("/public/envios/{id}/nuevanotificacion")
    public ResponseEntity nuevaNotificacionEnvio(@PathVariable("id") long id, @RequestParam() String notifi) {
        ujaPack.activarNotificacion(id, notifi);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

 */

}
