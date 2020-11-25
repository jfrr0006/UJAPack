package beans;

import entidades.Envio;
import entidades.Registro;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


@Repository
@Transactional
public class RepositorioEnvio {

    @PersistenceContext
    EntityManager em;

    public Envio buscar(long clave) {
        return em.find(Envio.class, clave);
    }

    public void insertar(Envio envio) {
        em.persist(envio);
    }

    public void actualizar(Envio envio) {
        em.merge(envio);
    }

    public void eliminar(Envio envio) {
        em.remove(em.merge(envio));
    }

    /**
     * Devolver todos los Envios
     *
     * @return listado de envios
     */
    public List<Envio> listEnvios() {

        List<Envio> envios = em.createQuery("Select e from Envio e ", Envio.class).getResultList();

        return envios;

    }

    /**
     * Devolver todos los Envios Extraviados
     *
     * @return listado de envios con estado extraviado
     */
    public List<Envio> listEnviosExtraviados() {

        List<Envio> envios = em.createQuery("Select e from Envio e where e.estado = 3", Envio.class).getResultList();

        return envios;

    }

    /**
     * Añade un registro a la ruta de un envio
     */
    public void añadirRegistro(Envio envio, Registro registro) {

        Envio cuentaEnlazada = em.merge(envio);

        cuentaEnlazada.getRuta().add(registro);

    }

    /**
     * Devuelve la lista de registros de la ruta de un envio
     *
     * @return listado Registros
     */
    public List<Registro> listRuta(long envio) {

        Envio envi = em.find(Envio.class, envio);

        return new ArrayList<>(envi.getRuta());

    }


}
