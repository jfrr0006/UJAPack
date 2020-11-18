package entidades.PuntoRuta;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Entity
public class CentroLog extends PuntoRuta {

    /* Localizacion */
    @NotBlank
    String localizacion;

    public CentroLog(int _id, String _nombre, String _localizacion) {
        super(_id, _nombre);
        this.localizacion = _localizacion;

    }

    public CentroLog() {

    }

    public String getLugar() {
        return localizacion;
    }


}
