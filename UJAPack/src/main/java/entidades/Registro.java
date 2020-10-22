package entidades;

import entidades.PuntoRuta.PuntoRuta;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public class Registro {

    /* Fecha de Registro */
    // @NotBlank
    LocalDateTime fecha;

    /* Si el Registro es de Entrada o Salida */
   // @NotBlank
    Boolean entrada;

    /* Registro del Punto de Ruta */
    @NotEmpty
    PuntoRuta puntoR;

    public Registro(PuntoRuta _puntoR){
        this.puntoR = _puntoR;
      //  this.entrada = _entrada;
      //  this.fecha = _fecha;

    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Boolean getEntrada() {
        return entrada;
    }

    public PuntoRuta getPuntoR() {
        return puntoR;
    }

    public void actualizarRegistro(LocalDateTime _fecha, Boolean _entrada) {
        this.entrada = _entrada;
        this.fecha = _fecha;

    }
}
