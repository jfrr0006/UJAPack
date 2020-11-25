package entidades.PuntoRuta;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class PuntoRuta {

    /* Int ID */
    @Id
    @Positive
    int id;

    /* Nombre */
    @NotBlank
    String nombre;

    /* Conexiones Puntos de Ruta*/
    @ManyToMany(fetch = FetchType.EAGER)// cascade=CascadeType.ALL ? , mappedBy = "conexiones"
    @MapKey(name = "id")
    Map<Integer, PuntoRuta> conexiones;

    public PuntoRuta(int _id, String _nombre) {
        this.id = _id;
        this.nombre = _nombre;
        this.conexiones = new HashMap<>();
    }

    public PuntoRuta() {

    }

    public int getId() {
        return id;
    }

    public Map<Integer, PuntoRuta> getConexiones() {
        return conexiones;
    }

    public String getLugar() {
        return nombre;
    }

    public void setConexion(PuntoRuta centro) {
        this.conexiones.put(centro.getId(), centro);
    }

}
