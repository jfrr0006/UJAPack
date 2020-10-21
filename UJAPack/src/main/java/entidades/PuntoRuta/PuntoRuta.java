package entidades.PuntoRuta;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

public abstract class PuntoRuta {

    /* Id de un Punto de Ruta */
    @NotBlank
    int id;

    /* Conexiones Puntos de Ruta*/
    @NotEmpty
    Map<Integer, PuntoRuta> conexiones;
}
