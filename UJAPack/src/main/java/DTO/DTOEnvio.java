package DTO;

import Utils.Estado;
import entidades.Envio;

import java.util.List;

public class DTOEnvio {

    public DTOEnvio(){

    }

    public DTOEnvio(Envio envi){
        this.id = envi.getId();
        this.importe = envi.getImporte();
        this.peso = envi.getPeso();
        this.dimensiones = envi.getDimensiones();
        this.remitente = envi.getRemitente();
        this.destinatario = envi.getDestinatario();
        this.datos_remitente =envi.getDatos_remitente();
        this.datos_destinatario=envi.getDatos_destinatario();
        this.notificacion = envi.getNotificacion();
        this.datos_notificacion = envi.getNotificacion();;
        this.estado = envi.getEstado();
        this.registroActual = envi.getRegistroActual();
    }
    /* Id del envio */
    private long id;

    /* Importe */
    private float importe;

    /* Peso */
    private float peso;

    /* Dimensiones */
    private float dimensiones;

    /* Lista de Registros de los Puntos */
    private List<String> ruta;

    /* Registro Actual */
    private int registroActual;

    /* Remitente */
    private  String remitente;

    /* Destinatario */
    private String destinatario;

    /* Datos Remitente */
    private String datos_remitente;

    /* Datos Destinatario */
    private String datos_destinatario;

    /* Punto de control donde se notificara */
    private String notificacion;

    /* Punto de control donde se notificara */
    private String datos_notificacion;

    /* Estado */
    private  Estado estado;

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

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getRuta() {
        return ruta;
    }

    public void setRuta(List<String> ruta) {
        this.ruta = ruta;
    }

    public int getRegistroActual() {
        return registroActual;
    }

    public void setRegistroActual(int registroActual) {
        this.registroActual = registroActual;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getDatos_notificacion() {
        return datos_notificacion;
    }

    public void setDatos_notificacion(String datos_notificacion) {
        this.datos_notificacion = datos_notificacion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }
}
