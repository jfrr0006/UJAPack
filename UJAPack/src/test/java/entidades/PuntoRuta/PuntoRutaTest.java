package entidades.PuntoRuta;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

class PuntoRutaTest {


    @Test
    void testValidacionOficina() {
        PuntoRuta punto = new Oficina(12345, "NombreA", "CentroA", "OficinaA");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PuntoRuta>> violations = validator.validate(punto);
        Assertions.assertThat(violations).isEmpty();

    }


    @Test
    void testValidacionCentroLog() {
        PuntoRuta punto = new CentroLog(12345, "CentroA", "OficinaA");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PuntoRuta>> violations = validator.validate(punto);
        Assertions.assertThat(violations).isEmpty();

    }


}