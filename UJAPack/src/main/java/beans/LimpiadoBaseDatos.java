package beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Servicio auxiliar de borrado de los datos en la base de datos (sólo para testing)
 */

@Service
public class LimpiadoBaseDatos {
    /**
     * Lista de entidades a borrar. NO metemos PuntoRuta ya que no vamos a tocar los puntos de ruta en un principio
     * Por lo que se pueden insertar la primera vez y ya estan cargados para el resto de test
     */
    final String[] entidades = {
            "Registro",
            "Envio"
            //"hibernate_sequence"
    };
    final String deleteFrom = "delete from ";
    @PersistenceContext
    EntityManager em;
    @Autowired
    TransactionTemplate transactionTemplate;

    /**
     * Realizar borrado
     */
    public void limpiar() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            for (String tabla : entidades) {
                em.createQuery(deleteFrom + tabla).executeUpdate();
            }
        });
    }
}
