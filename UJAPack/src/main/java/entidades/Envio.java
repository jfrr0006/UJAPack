package entidades;

import utils.Estado;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;


public class Envio {

    /* Id de envio */
    @NotBlank
    long id;

    /* Importe */
    @NotBlank
    float importe;

    /* Lista de Registros de los Puntos */
    @NotEmpty
    List<Registro> ruta;

    /* Registro Actual */
    @NotEmpty
    int registroActual;

    /* Peso */
    @Positive
    float peso;

    /* Peso */
    @Positive
    float dimensiones;

    /* Remitente */
    @NotBlank
    String remitente;

    /* Destinatario */
    @NotBlank
    String destinatario;

    /* Punto de control donde se notificara */
    @NotBlank
    String notificacion;

    /* Estado */
    Estado estado;

    public Envio(long _id, float _importe, List<Registro> _ruta, float _peso, float _dimensiones, String _remitente, String _destinatario){
        this.id = _id;
        this.importe = _importe;
        this.ruta =_ruta;
        this.peso =_peso;
        this.dimensiones =_dimensiones;
        this.remitente = _remitente;
        this.destinatario = _destinatario;
        this.estado = Estado.EnTransito;
        this.registroActual=0;
        this.notificacion="Ninguna";

    }


    public long getId() {
        return id;
    }

    public float getImporte() {
        return importe;
    }

    public List<Registro> getRuta() {
        return ruta;
    }

    public Estado getEstado() { return estado; }

    public int getRegistroActual() {
        return registroActual;
    }

    public void avanzarRegistroActual() {
        this.registroActual++;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion=notificacion;
    }

    public String getNotificacion() {
        return notificacion;
    }
}
