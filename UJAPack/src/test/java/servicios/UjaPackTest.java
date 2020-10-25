package servicios;

import DAE.UJAPack.UjaPackApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UjaPackApplication.class)

class UjaPackTest {

    @Autowired
    UjaPack servicioUjaPack;



    @Test
    public void testServicioUjaPack() {
        Assertions.assertThat(servicioUjaPack).isNotNull();
    }


    @Test
    void generarEnvio() {
        String remi1="";
        String desti1="Barcelona";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        servicioUjaPack.generarEnvio(remi1,desti1,peso1,dimen1);

        Assertions.assertThat(servicioUjaPack.envios.size()).isEqualTo(1);



    }


}