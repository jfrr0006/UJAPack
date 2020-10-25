package servicios;

import DAE.UJAPack.UjaPackApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@SpringBootTest(classes = UjaPackApplication.class)

class UjaPackTest {

   // @Autowired
    UjaPack servicioUjaPack;

    UjaPackTest() throws IOException {
        servicioUjaPack= new UjaPack();
    }


    @Test
    public void testServicioUjaPack() {
        Assertions.assertThat(servicioUjaPack).isNotNull();
    }


    @Test
    void generarEnvio() {
        String remi1="Ceuta";
        String desti1="Barcelona";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;


        Assertions.assertThatThrownBy(() -> {
            servicioUjaPack.generarEnvio(remi1,desti1,peso1,dimen1);})
                .isInstanceOf(ConstraintViolationException.class);



    }


}