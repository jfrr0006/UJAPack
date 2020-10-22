package entidades.PuntoRuta;

import javax.validation.constraints.NotBlank;

public class Oficina extends PuntoRuta {

    /* Nombre Centro logistico */
    @NotBlank
    String centroLog;

    /* Nombre Oficina */
    @NotBlank
    String oficina;

    public Oficina(int _id, String _nombre,String _centroLog,String _oficina) {
        super(_id,_nombre);
        this.oficina = _oficina;
        this.centroLog = _centroLog;
    }

    public String getLugar() {
        return oficina;
    }

    public String getCentroLog() {
        return centroLog;
    }
    public void setConexion(Oficina oficina) {
        this.conexiones.put(oficina.getId(),oficina);
    }
}
