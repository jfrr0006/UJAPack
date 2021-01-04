package entidades;

import entidades.PuntoRuta.PuntoRuta;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
//@Access(AccessType.FIELD)
public class Registro {
    // static final String STRING_EN = "E";
    // static final String STRING_SA = "S";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id_registro;

    /* Fecha de Registro */
    @PastOrPresent
    LocalDateTime fecha;

    /* Si el Registro es de Entrada o Salida */
    //@Transient
    boolean entrada;

    /* Registro del Punto de Ruta */
    @ManyToOne
    @JoinColumn(name = "fk_puntoRuta")
    PuntoRuta puntoR;

    public Registro(PuntoRuta _puntoR) {
        this.puntoR = _puntoR;
    }

    public Registro() {

    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean getEntrada() {
        return entrada;
    }

    public PuntoRuta getPuntoR() {
        return puntoR;
    }

    public void actualizarRegistro(LocalDateTime _fecha, boolean _entrada) {
        this.entrada = _entrada;
        this.fecha = _fecha;

    }
    /*
    @Access(AccessType.PROPERTY)
    @Column(name = "entrada")
    public String getEntradaString() {
        return entrada ? STRING_EN : STRING_SA;
    }

    public void actualizarRegistroString( LocalDateTime _fecha, String _entrada) {
        this.entrada = _entrada.equals(STRING_EN);
        this.fecha = _fecha;

    }
    */
}
