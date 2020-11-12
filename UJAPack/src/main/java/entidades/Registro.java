package entidades;

import entidades.PuntoRuta.PuntoRuta;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Registro {

    /* Fecha de Registro */
    LocalDateTime fecha;

    /* Si el Registro es de Entrada o Salida */
    Boolean entrada;

    /* Registro del Punto de Ruta */
    PuntoRuta puntoR;

    public Registro(PuntoRuta _puntoR) {
        this.puntoR = _puntoR;
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

    public void actualizarRegistro(@NotNull LocalDateTime _fecha,@NotNull Boolean _entrada) {
        this.entrada = _entrada;
        this.fecha = _fecha;

    }
}
