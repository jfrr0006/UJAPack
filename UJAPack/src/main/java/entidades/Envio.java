package entidades;

import Utils.Estado;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Entity
public class Envio {

    /* Id de envio */
    @Id
    @Positive
    long id;

    /* Importe */
    @Positive
    float importe;

    /* Lista de Registros de los Puntos */
    @OneToMany
    @JoinColumn(name = "envioId")
    List<Registro> ruta;

    /* Registro Actual */
    @NotNull
    int registroActual;

    /* Peso */
    @Positive
    float peso;

    /* Dimensiones */
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

    /* Estado */
    Estado estado;

    /* Se ha quitado la parte de notificaciones */

    /* Punto de control donde se notificara */
    //@NotBlank
    //String notificacion;

    /* Notificacion */
    //@NotBlank
    //String datos_notificacion;


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
        //this.notificacion = "Ninguna";
        //this.datos_notificacion = "Nada";

    }

    public Envio() {

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

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public float getPeso() {
        return peso;
    }

    public float getDimensiones() {
        return dimensiones;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getDatos_remitente() {
        return datos_remitente;
    }

    public String getDatos_destinatario() {
        return datos_destinatario;
    }

    public int getRegistroActual() {
        return registroActual;
    }

    public void avanzarRegistroActual() {
        this.registroActual++;
    }


    /**
     * Modifica el lugar para notificar cuando llegue/salga de este
     *
     * @param notificacion el nombre del Punto de Ruta, EJ: Ceuta
     */

   /* public String getNotificacion() {
       return notificacion;
   }
    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getDatosNotificacion() {
        return datos_notificacion;
    }

    public void setDatosNotificacion(String d_notificacion) {
        this.datos_notificacion = d_notificacion;
    }

    */
}
