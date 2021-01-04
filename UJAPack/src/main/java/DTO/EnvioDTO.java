package DTO;

import Utils.Estado;
import org.springframework.hateoas.Link;

public class EnvioDTO {

    /*Si quitamos informacion del DTO muchos del los TEST no se podrian hacer, hay datos como notificacion que son prescindibles de enseñar pero
    necesitamos tenerlo en el GET para poder luego comprobarlo, en el Cliente Angular no lo mostraremos */

    /* Id del envio */
    private long id;
    /* Importe */
    private float importe;
    /* Peso */
    private float peso;
    /* Dimensiones */
    private float dimensiones;
    /* Lista de Registros de los Puntos */
    private Link ruta;
    /* Registro Actual */
    private int registroActual;
    /* Remitente */
    private String remitente;
    /* Destinatario */
    private String destinatario;
    /* Datos Remitente */
    private String datos_remitente;
    /* Datos Destinatario */
    private String datos_destinatario;
    /* Estado */
    private Estado estado;

    /* Punto de control donde se notificara */
    // private String notificacion;
    /* Notificacion */
    //  private String datos_notificacion;

    public EnvioDTO() {

    }

    public EnvioDTO(long _id, float _importe, float _peso, float _dimensiones, String _remitente, String _destinatario, String _datos_remitente,
                    String _datos_destinatario, Estado _estado, int _registroactual) {
        this.id = _id;
        this.setImporte(_importe);
        this.peso = _peso;
        this.dimensiones = _dimensiones;
        this.remitente = _remitente;
        this.destinatario = _destinatario;
        this.datos_remitente = _datos_remitente;
        this.datos_destinatario = _datos_destinatario;
        this.estado = _estado;
        this.setRegistroActual(_registroactual);
        //  this.notificacion = _notificacion;
        // this.datos_notificacion = _datos_notificacion;
    }

    public EnvioDTO(String _remitente, String _destinatario, float _peso, float _dimensiones, String _datos_remitente,
                    String _datos_destinatario) {
        this.peso = _peso;
        this.dimensiones = _dimensiones;
        this.remitente = _remitente;
        this.destinatario = _destinatario;
        this.datos_remitente = _datos_remitente;
        this.datos_destinatario = _datos_destinatario;
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

    public long getId() {
        return id;
    }

    public Link getRuta() {
        return ruta;
    }

    public void setRuta(Link _ruta) {
        this.ruta = _ruta;
    }

    public Estado getEstado() {
        return estado;
    }


    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public int getRegistroActual() {
        return registroActual;
    }

    public void setRegistroActual(int registroActual) {
        this.registroActual = registroActual;
    }
}
