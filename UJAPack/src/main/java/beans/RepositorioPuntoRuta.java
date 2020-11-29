package beans;

import entidades.PuntoRuta.PuntoRuta;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class RepositorioPuntoRuta {

    @PersistenceContext
    EntityManager em;

    public PuntoRuta buscar(int clave) {
        return em.find(PuntoRuta.class, clave);
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
    @Cacheable(value = "puntosRuta")
    public List<PuntoRuta> listPuntosRuta() {

        List<PuntoRuta> puntos = em.createQuery("Select p from PuntoRuta p ", PuntoRuta.class).getResultList();

        return puntos;

    }

}
