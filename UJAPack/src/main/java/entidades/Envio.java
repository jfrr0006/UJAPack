package entidades;

import Utils.Estado;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


public class Envio {

    /* Id de envio */
    @Positive
    long id;

    /* Importe */
    @Positive
    float importe;

    /* Lista de Registros de los Puntos */
    @NotEmpty
    List<Registro> ruta;

    /* Registro Actual */
    @NotNull
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

    /* Datos Remitente */
    @NotBlank
    String datos_remitente;

    /* Datos Destinatario */
    @NotBlank
    String datos_destinatario;

    /* Punto de control donde se notificara */
    @NotBlank
    String notificacion;

    /* Estado */
    Estado estado;

    public Envio(long _id, float _importe, List<Registro> _ruta, float _peso, float _dimensiones, String _remitente, String _destinatario, String _datos_remitente, String _datos_destinatario) {
        this.id = _id;
        this.importe = _importe;
        this.ruta = _ruta;
        this.peso = _peso;
        this.dimensiones = _dimensiones;
        this.remitente = _remitente;
        this.destinatario = _destinatario;
        this.datos_remitente = _datos_remitente;
        this.datos_destinatario = _datos_destinatario;
        this.estado = Estado.EnTransito;
        this.registroActual = 0;
        this.notificacion = "Ninguna";

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

    public Estado getEstado() {
        return estado;
    }

    public int getRegistroActual() {
        return registroActual;
    }

    public void avanzarRegistroActual() {
        this.registroActual++;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    /**
     * Modifica el lugar para notificar cuando llegue/salga de este
     * @param notificacion el nombre del Punto de Ruta, EJ: Ceuta
     */

    public void setNotificacion(@NotBlank String notificacion) {
        this.notificacion = notificacion;
    }

    public String getNotificacion() {
        return notificacion;
    }
}
