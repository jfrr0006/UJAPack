package beans;

import entidades.Envio;
import entidades.Registro;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public void añadirRegistro(Envio envio, Registro registro){

        Envio cuentaEnlazada = em.merge(envio);

        cuentaEnlazada.getRuta().add(registro);

    }



}
