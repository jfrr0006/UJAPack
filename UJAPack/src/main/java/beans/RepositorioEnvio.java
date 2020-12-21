package beans;

import entidades.Envio;
import entidades.Registro;
import excepciones.EnvioNoRegistrado;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioEnvio {

    @PersistenceContext
    EntityManager em;

   // @Cacheable(value = "envios", key = "#clave") // Preguntar da problemas aunque no es eternal
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Envio> buscar(long clave) { return Optional.ofNullable(em.find(Envio.class, clave)); }

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

        return em.createQuery("Select e from Envio e ", Envio.class).getResultList();

    }

    /**
     * Devolver todos los Envios Extraviados
     *
     * @return listado de envios con estado extraviado
     */
    public List<Envio> listEnviosExtraviados() {

        return em.createQuery("Select e from Envio e where e.estado = 3", Envio.class).getResultList();

    }


    /**
     * Devuelve la lista de registros de la ruta de un envio
     *
     * @return listado Registros
     */
    @Cacheable(value = "enviosRuta", key = "#clave")
    public List<Registro> listRuta(long clave) {

        Optional<Envio> envi = Optional.ofNullable(em.find(Envio.class, clave));

        return new ArrayList<>(envi.orElseThrow(EnvioNoRegistrado::new).getRuta());

    }


}
