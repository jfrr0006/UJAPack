package servicios;

import DAE.UJAPack.UjaPackApplication;
import entidades.Envio;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import utils.Estado;

import java.io.IOException;
import java.util.List;


@SpringBootTest(classes = UjaPackApplication.class)

class UjaPackTest {


    UjaPack servicioUjaPack;

    @Autowired
    UjaPackTest() throws IOException {
        servicioUjaPack= new UjaPack();
    }

    @Test
    public void testServicioUjaPack() {
        Assertions.assertThat(servicioUjaPack).isNotNull();
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void generarEnvioTest() {
        String remi1="Ceuta";
        String desti1="Barcelona";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio=servicioUjaPack.generarEnvio(remi1,desti1,peso1,dimen1);

        System.out.println("Id: "+envio.getId()+" Importe: "+envio.getImporte());
        Assertions.assertThat(envio.getId()).isNotNull();
        Assertions.assertThat(envio.getImporte()).isNotNull();
        Assertions.assertThat(envio.getImporte()).isPositive();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void notificacionPuntoControl() {
        String remi1="Ceuta";
        String desti1="Barcelona";
        String notificacion="Barcelona";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio=servicioUjaPack.generarEnvio(remi1,desti1,peso1,dimen1);
        servicioUjaPack.activarNotificacion(envio.getId(),notificacion);

        for (int i = 0; i < envio.getRuta().size()+2; i++) {//Nos aseguramos que va a avanzar el envio en su totalidad
            servicioUjaPack.avanzarEnvios();

        }

        Assertions.assertThat(envio.getEstado()).isEqualByComparingTo(Estado.Entregado);//Nos aseguramos que ha sido entregado

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void obtenerSituacionEnvio() {
        String remi1="Ceuta";
        String desti1="Barcelona";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio=servicioUjaPack.generarEnvio(remi1,desti1,peso1,dimen1);

        for (int i = 0; i < envio.getRuta().size()/2; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            servicioUjaPack.avanzarEnvios();

        }

        String situacion=servicioUjaPack.situacionActualEnvio(envio.getId());
        List<String> listado = servicioUjaPack.listadoRutaEnvio(envio.getId());

        System.out.println("Última actualización--->"+situacion);
        System.out.println("Listado:");

        for (String list: listado) {
            System.out.println(list);

        }

        Assertions.assertThat(situacion).isNotBlank();
        Assertions.assertThat(listado).isNotEmpty();

    }

}