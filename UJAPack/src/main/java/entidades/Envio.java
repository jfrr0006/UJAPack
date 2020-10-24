package entidades;

import Utils.Estado;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
    @NotBlank
    float peso;

    /* Peso */
    @NotBlank
    float dimensiones;

    /* Remitente */
    @NotBlank
    String remitente;

    /* Destinatario */
    @NotBlank
    String destinatario;

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

    public float getPeso() {
        return peso;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public float getDimensiones() { return dimensiones; }

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
}
