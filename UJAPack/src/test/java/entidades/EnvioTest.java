package entidades;

import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.PuntoRuta;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


class EnvioTest {



    @Test
    void testValidacionEnvio(){
        PuntoRuta punto = new CentroLog(12345,"CentroA","OficinaA");
        Registro registro =new Registro(punto);
        List<Registro> registros = new ArrayList<>();
        registros.add(registro);

        Envio envio = new Envio(1234567890,20,registros,35,15,"Ceuta","Barcelona","Gepeto Marin - Atlantida 66667 - Calle Falsa 123","Pinocho Marin - Ballena 66668 - Avenida Esofago 123");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Envio>> violations = validator.validate(envio);
        Assertions.assertThat(violations).isEmpty();

    }


}