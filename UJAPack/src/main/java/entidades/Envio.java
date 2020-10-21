package entidades;
import javax.swing.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


public class Envio {

    /* Id de envio */
    @NotBlank
    int id;

    /* Importe */
    @NotBlank
    float importe;

    /* Lista de Registros de los Puntos */
    @NotEmpty
    List<Registro> ruta;

    /* Peso */
    @NotBlank
    float peso;

    /* Remitente */
    @NotBlank
    String remitente;

    /* Destinatario */
    @NotBlank
    String destinatario;

    /* Estado */
   // Enum estado;

    public Envio(int _id, float _importe, List<Registro> _ruta, float _peso, String _remitente, String _destinatario){
        this.id = _id;
        this.importe = _importe;
        this.ruta =_ruta;
        this.peso =_peso;
        this.remitente = _remitente;
        this.destinatario = _destinatario;

    }

    public int getId() {
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



}
