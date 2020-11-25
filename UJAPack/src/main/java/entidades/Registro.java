package entidades;

import entidades.PuntoRuta.PuntoRuta;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
//@Access(AccessType.FIELD)
public class Registro {
    // static final String STRING_EN = "E";
    // static final String STRING_SA = "S";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //Adrian creo que dijo que en sqlite no funcionaba bien el autoincrementar
    int id_registro;

    /* Fecha de Registro */
    @PastOrPresent
    LocalDateTime fecha;

    /* Si el Registro es de Entrada o Salida */
    //@Transient
    Boolean entrada;

    /* Registro del Punto de Ruta */
    @OneToOne
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

    /*
    @Access(AccessType.PROPERTY)
    @Column(name = "entrada")
    public String getEntradaString() {
        return entrada ? STRING_EN : STRING_SA;
    } */

    public Boolean getEntrada() {
        return entrada;
    }

    public PuntoRuta getPuntoR() {
        return puntoR;
    }

    public void actualizarRegistro(@NotNull LocalDateTime _fecha, @NotNull Boolean _entrada) {
        this.entrada = _entrada;
        this.fecha = _fecha;

    }

    /*
    public void actualizarRegistroString(@NotNull LocalDateTime _fecha,@NotNull String _entrada) {
        this.entrada = _entrada.equals(STRING_EN);
        this.fecha = _fecha;

    }
    */
}
