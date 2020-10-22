package entidades.PuntoRuta;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

public abstract class PuntoRuta {

    /* Int ID */
    @NotBlank
    int id;

    /* Nombre */
    @NotBlank
    String nombre;

    /* Conexiones Puntos de Ruta*/
   // @NotEmpty
    Map<Integer, PuntoRuta> conexiones;

    public PuntoRuta(int _id,String _nombre){
        this.id = _id;
        this.nombre =_nombre;
        this.conexiones = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Map<Integer, PuntoRuta> getConexiones() {
        return conexiones;
    }

    public void setConexiones(Map<Integer, PuntoRuta> conexiones) {
        this.conexiones = conexiones;
    }

    public void setConexion(CentroLog centro) {
        this.conexiones.put(centro.getId(),centro);
    }
    public void setConexion(PuntoRuta centro) {
        this.conexiones.put(centro.getId(),centro);
    }
    public void setConexion(Oficina oficina) {
        this.conexiones.put(oficina.getId(),oficina);
    }
}
