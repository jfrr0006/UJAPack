package servicios;

import DAE.UJAPack.UjaPackApplication;
import Utils.Estado;
import beans.LimpiadoBaseDatos;
import beans.UjaPack;
import entidades.Envio;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@SpringBootTest(classes = UjaPackApplication.class)
@ActiveProfiles(profiles = "test")
class UjaPackTest {

    @Autowired
    UjaPack servicioUjaPack;

    @Autowired
    LimpiadoBaseDatos limpiadorBaseDatos;

    @Test
    public void testServicioUjaPack() {
        Assertions.assertThat(servicioUjaPack).isNotNull();
    }


    @Test
    void generarEnvioTest() {

     /*
     Entendemos que es mas limpio meterlos directamente inline en generar envio, pero vamos a dejar esto en comentario como leyenda
        String remitente = "Ceuta";
        String destinatario = "Barcelona";
        String datos_remitente="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_destinatario="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso = 5.0f;
        Float dimensiones = 10.0f;
      */
        Envio envio = servicioUjaPack.generarEnvio(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto1 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho1 Marin - Ballena 66668 - Avenida Esofago 123");

        Assertions.assertThat(envio.getId()).isNotNull();
        Assertions.assertThat(envio.getImporte()).isNotNull();
        Assertions.assertThat(envio.getImporte()).isPositive();

    }

    @Test
    void obtenerSituacionEnvio() {

        Envio envio = servicioUjaPack.generarEnvio(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto3 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho3 Marin - Ballena 66668 - Avenida Esofago 123");
        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            servicioUjaPack.avanzarEnvios();

        }

        String situacion = servicioUjaPack.situacionActualEnvio(envio.getId());
        List<String> listado = servicioUjaPack.listadoRutaEnvio(envio.getId());

        Assertions.assertThat(situacion).isNotBlank();
        Assertions.assertThat(listado).isNotEmpty();

    }

    @Test
    void generarEnvioExtraviado() {

        Envio envio = servicioUjaPack.generarEnvio(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto4 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho4 Marin - Ballena 66668 - Avenida Esofago 123");

        for (int i = 0; i < 5; i++) {//Mitad del camino
            servicioUjaPack.avanzarEnvioID(envio.getId());

        }

        servicioUjaPack.actualizarEnviosExtraviadosTest();
        envio = servicioUjaPack.verEnvio(envio.getId());
        List<Envio> enviosExtra = servicioUjaPack.consultarEnviosExtraviados();
        Assertions.assertThat(envio.getEstado()).isEqualByComparingTo(Estado.Extraviado);
        Assertions.assertThat(enviosExtra).isNotEmpty();

    }

    @Test
    void consultaEnviosExtraviados() {

        servicioUjaPack.generarEnvio(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto5 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho5 Marin - Ballena 66668 - Avenida Esofago 123");

        servicioUjaPack.generarEnvio(
                "Madrid",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto6 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho6 Marin - Ballena 66668 - Avenida Esofago 123");

        for (int i = 0; i < 5; i++) {//Mitad del camino
            servicioUjaPack.avanzarEnvios();

        }

        Envio envio3 = servicioUjaPack.generarEnvio(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "Gepeto7 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho7 Marin - Ballena 66668 - Avenida Esofago 123");
        servicioUjaPack.generarEnvio(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "Gepeto7_2 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho7_2 Marin - Ballena 66668 - Avenida Esofago 123");

        servicioUjaPack.actualizarEnviosExtraviadosTest();


        Assertions.assertThat(envio3.getEstado()).isNotEqualByComparingTo(Estado.Extraviado);
        Assertions.assertThat(servicioUjaPack.consultarEnviosExtraviados(LocalDateTime.now().minus(1, ChronoUnit.DAYS), LocalDateTime.now()).size()).isGreaterThan(0);
        Assertions.assertThat(servicioUjaPack.consultarEnviosExtraviados(LocalDateTime.parse("1900-12-31T00:00:00"), LocalDateTime.parse("1901-12-31T00:00:00")).size()).isEqualTo(0);


    }

    @Test
    void consultaPorcentajeEnviosExtraviados() {

        servicioUjaPack.generarEnvio(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto8 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho8 Marin - Ballena 66668 - Avenida Esofago 123");
        servicioUjaPack.generarEnvio(
                "Madrid",
                "Barcelona",
                5.0f, 10.0f,
                "Gepeto9 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho9 Marin - Ballena 66668 - Avenida Esofago 123");

        for (int i = 0; i < 5; i++) {//Mitad del camino
            servicioUjaPack.avanzarEnvios();

        }

        servicioUjaPack.generarEnvio(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "Gepeto10 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho10 Marin - Ballena 66668 - Avenida Esofago 123");

        servicioUjaPack.generarEnvio(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "Gepeto11 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho11 Marin - Ballena 66668 - Avenida Esofago 123");

        servicioUjaPack.actualizarEnviosExtraviadosTest();
        double enviosExtr = servicioUjaPack.porcentajeEnviosExtraviados("dia");

        Assertions.assertThat(enviosExtr).isPositive();
        Assertions.assertThat(enviosExtr).isLessThan(100);
        Assertions.assertThat(servicioUjaPack.consultarEnviosExtraviados(LocalDateTime.parse("1900-12-31T00:00:00"), LocalDateTime.parse("1901-12-31T00:00:00")).size()).isEqualTo(0);


    }

    @BeforeEach
    void limpiarBaseDatos() {
        limpiadorBaseDatos.limpiar();
    }


    /* Parte Notificaciones
     @Test
    void notificacionPuntoControl() {


        Envio envio = servicioUjaPack.generarEnvio(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "Gepeto2 Marin - Atlantida 66667 - Calle Falsa 123",
                "Pinocho2 Marin - Ballena 66668 - Avenida Esofago 123");
        //   servicioUjaPack.activarNotificacion(envio.getId(), "Barcelona");

        for (int i = 0; i < 30; i++) {//Nos aseguramos que va a avanzar el envio en su totalidad
            servicioUjaPack.avanzarEnvios();

        }
        envio = servicioUjaPack.verEnvio(envio.getId());
        Assertions.assertThat(envio.getEstado()).isEqualByComparingTo(Estado.Entregado);//Nos aseguramos que ha sido entregado
        //  Assertions.assertThat(envio.getDatosNotificacion()).contains("El envio");
        Assertions.assertThat(envio.getRuta()).isNotEmpty();
        Assertions.assertThat(envio).isNotNull();

    }
     */
}
