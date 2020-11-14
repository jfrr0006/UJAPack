package servicios;

import DAE.UJAPack.UjaPackApplication;
import Utils.Estado;
import entidades.Envio;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.within;


@SpringBootTest(classes = UjaPackApplication.class)
class UjaPackTest {

    @Autowired
    UjaPack servicioUjaPack;

    @Test
    public void testServicioUjaPack() {
        Assertions.assertThat(servicioUjaPack).isNotNull();
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void generarEnvioTest() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";
        String datos_remi1="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_desti1="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        Assertions.assertThat(envio.getId()).isNotNull();
        Assertions.assertThat(envio.getImporte()).isNotNull();
        Assertions.assertThat(envio.getImporte()).isPositive();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void notificacionPuntoControl() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";
        String datos_remi1="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_desti1="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        String notificacion = "Barcelona";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);
        servicioUjaPack.activarNotificacion(envio.getId(), notificacion);
        for (int i = 0; i < envio.getRuta().size() + 2; i++) {//Nos aseguramos que va a avanzar el envio en su totalidad
            servicioUjaPack.avanzarEnvios();

        }

        Assertions.assertThat(envio.getEstado()).isEqualByComparingTo(Estado.Entregado);//Nos aseguramos que ha sido entregado
        Assertions.assertThat(envio.getRuta()).isNotEmpty();
        Assertions.assertThat(envio).isNotNull();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void obtenerSituacionEnvio() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";
        String datos_remi1="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_desti1="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);
        for (int i = 0; i < envio.getRuta().size() / 2; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            servicioUjaPack.avanzarEnvios();

        }
        String situacion = servicioUjaPack.situacionActualEnvio(envio.getId());
        List<String> listado = servicioUjaPack.listadoRutaEnvio(envio.getId());

        Assertions.assertThat(situacion).isNotBlank();
        Assertions.assertThat(listado).isNotEmpty();

    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void generarEnvioExtraviado() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";
        String datos_remi1="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_desti1="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);
        for (int i = 0; i < envio.getRuta().size() / 2; i++) {//Mitad del camino
            servicioUjaPack.avanzarEnvios();

        }
        LocalDateTime ahora = LocalDateTime.parse("2020-12-31T00:00:00");
        servicioUjaPack.actualizarEnviosExtraviados(ahora);

        Assertions.assertThat(envio.getEstado()).isEqualByComparingTo(Estado.Extraviado);
        Assertions.assertThat(servicioUjaPack.consultarEnviosExtraviados()).isNotEmpty();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void consultaEnviosExtraviados() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";
        String datos_remi1="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_desti1="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        remi1 = "Madrid";
        desti1 = "Barcelona";
        Envio envio2 = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        for (int i = 0; i < envio.getRuta().size() / 2; i++) {//Mitad del camino
            servicioUjaPack.avanzarEnvios();

        }

        remi1 = "Barcelona";
        desti1 = "Toledo";
        Envio envio3 = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        LocalDateTime ahora = LocalDateTime.parse("2020-12-31T00:00:00");
        servicioUjaPack.actualizarEnviosExtraviados(ahora);


        Assertions.assertThat(envio3.getEstado()).isNotEqualByComparingTo(Estado.Extraviado);
        Assertions.assertThat(servicioUjaPack.consultarEnviosExtraviados(LocalDateTime.now().minus(1, ChronoUnit.DAYS),LocalDateTime.now()).size()).isEqualTo(2);
        Assertions.assertThat(servicioUjaPack.consultarEnviosExtraviados(LocalDateTime.parse("1900-12-31T00:00:00"),LocalDateTime.parse("1901-12-31T00:00:00")).size()).isEqualTo(0);


    }
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void consultaPorcentajeEnviosExtraviados() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";
        String datos_remi1="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_desti1="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso1 = 5.0f;
        Float dimen1 = 10.0f;
        Envio envio = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        remi1 = "Madrid";
        desti1 = "Barcelona";
        Envio envio2 = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        for (int i = 0; i < envio.getRuta().size() / 2; i++) {//Mitad del camino
            servicioUjaPack.avanzarEnvios();

        }

        remi1 = "Barcelona";
        desti1 = "Toledo";
        Envio envio3 = servicioUjaPack.generarEnvio(remi1, desti1, peso1, dimen1,datos_remi1,datos_desti1);

        LocalDateTime ahora = LocalDateTime.parse("2020-12-31T00:00:00");
        servicioUjaPack.actualizarEnviosExtraviados(ahora);


        Assertions.assertThat(servicioUjaPack.porcentajeEnviosExtraviados("dia")).isCloseTo(66.66,within(0.1));
        Assertions.assertThat(servicioUjaPack.porcentajeEnviosExtraviados("dia")).isPositive();
        Assertions.assertThat(servicioUjaPack.porcentajeEnviosExtraviados("dia")).isLessThan(100);


    }


}