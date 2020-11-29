package beans;

import entidades.Registro;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class RepositorioRegistro {

    @PersistenceContext
    EntityManager em;

    public Registro buscar(int clave) {
        return em.find(Registro.class, clave);
    }

    public void insertar(Registro registro) {
        em.persist(registro);
    }

    public void actualizar(Registro registro) {
        em.merge(registro);
    }

    public void eliminar(Registro registro) {
        em.remove(em.merge(registro));
    }

}
