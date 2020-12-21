package beans;

import entidades.PuntoRuta.PuntoRuta;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioPuntoRuta {

    @PersistenceContext
    EntityManager em;

    @Cacheable(value = "puntosRuta", key = "#clave")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<PuntoRuta> buscar(int clave) {
        return Optional.ofNullable(em.find(PuntoRuta.class, clave));
    }

    public void insertar(PuntoRuta punto) {
        em.persist(punto);
    }

    public void actualizar(PuntoRuta punto) {
        em.merge(punto);
    }

    public void eliminar(PuntoRuta punto) {
        em.remove(em.merge(punto));
    }

    /**
     * Devolver todos los Puntos de ruta
     *
     * @return listado de puntos de ruta
     */
    public List<PuntoRuta> listPuntosRuta() {

        return em.createQuery("Select p from PuntoRuta p ", PuntoRuta.class).getResultList();

    }


}
