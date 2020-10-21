package entidades;

import entidades.PuntoRuta.PuntoRuta;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public class Registro {

    /* Fecha de Registro */
    @NotBlank
    LocalDateTime fecha;

    /* Si el Registro es de Entrada o Salida */
    @NotBlank
    Boolean entrada;

    /* Registro del Punto de Ruta */
    @NotEmpty
    PuntoRuta puntoR;

    public Registro(PuntoRuta _puntoR, LocalDateTime _fecha, Boolean _entrada){
        this.puntoR = _puntoR;
        this.entrada = _entrada;
        this.fecha = _fecha;

    }



}
