package REST;

import DAE.UJAPack.UjaPackApplication;
import DTO.EnvioDTO;
import DTO.RespuestaDTO;
import Utils.Estado;
import beans.LimpiadoBaseDatos;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.within;


@SpringBootTest(classes = UjaPackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
class ServicioRestTest {

    @Autowired
    LimpiadoBaseDatos limpiadorBaseDatos;

    @LocalServerPort
    int localPort;

    @Autowired
    MappingJackson2HttpMessageConverter springBootJacksonConverter;

    TestRestTemplate restTemplate;

    /**
     * Crear un TestRestTemplate para las pruebas
     */
    @PostConstruct
    void crearRestTemplate() {

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort + "/ujapack")
                .additionalMessageConverters(List.of(springBootJacksonConverter)).basicAuthentication("admin", "admin");

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }


    @Test
    void generarEnvioTest() {
     /*
        String remitente = "Ceuta";
        String destinatario = "Barcelona";
        String datos_remitente="Gepeto Marin - Atlantida 66667 - Calle Falsa 123";
        String datos_destinatario="Pinocho Marin - Ballena 66668 - Avenida Esofago 123";
        Float peso = 5.0f;
        Float dimensiones = 10.0f;
      */
        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST1 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST1 Marin - Ballena 66668 - Avenida Esofago 123");


        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/envios/private/nuevoenvio", envio, EnvioDTO.class);

        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    }

    @Test
    void notificacionPuntoControl() {


        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST2 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST2 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/envios/private/nuevoenvio", envio, EnvioDTO.class);


        ResponseEntity respuestaPUT = restTemplate.exchange("/envios/public/{id}/nuevanotificacion?notifi=Barcelona", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());

        for (int i = 0; i < 30; i++) {//Nos aseguramos que va a avanzar el envio en su totalidad
            restTemplate.exchange("/envios/private/{id}/siguientepunto", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());

        }

        ResponseEntity<EnvioDTO> respuestaGET = restTemplate.getForEntity("/envios/public/{id}", EnvioDTO.class, respuestaPOST.getBody().getId());

        Assertions.assertThat(respuestaGET.getBody().getEstado()).isEqualByComparingTo(Estado.Entregado);//Nos aseguramos que ha sido entregado
        Assertions.assertThat(respuestaGET.getBody().getDatos_notificacion()).contains("El envio");
        Assertions.assertThat(respuestaGET.getBody()).isNotNull();
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaGET.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);


    }

    @Test
    void obtenerSituacionEnvio() {

        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST3 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST3 Marin - Ballena 66668 - Avenida Esofago 123");
        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/envios/private/nuevoenvio", envio, EnvioDTO.class);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            restTemplate.exchange("/envios/private/{id}/siguientepunto", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());

        }
        ResponseEntity<RespuestaDTO> respuestaGETactual = restTemplate.getForEntity("/envios/public/{id}/actual", RespuestaDTO.class, respuestaPOST.getBody().getId());

        ResponseEntity<List> respuestaGETruta = restTemplate.getForEntity("/envios/public/{id}/ruta", List.class, respuestaPOST.getBody().getId());


        Assertions.assertThat(respuestaGETactual.getBody().getMensaje()).isNotBlank();
        Assertions.assertThat(respuestaGETruta.getBody()).isNotEmpty();
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaGETactual.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGETruta.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void generarEnvioExtraviado() {

        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST4 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST4 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/envios/private/nuevoenvio", envio, EnvioDTO.class);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            restTemplate.exchange("/envios/private/{id}/siguientepunto", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());

        }

        restTemplate.exchange("/envios/private/testextraviados?_fecha=2021-12-31T00:00:00", HttpMethod.PUT, null, Void.class);
        ResponseEntity<List> respuestaGET = restTemplate.getForEntity("/envios/private/extraviados", List.class);
        ResponseEntity<EnvioDTO> respuestaGET2 = restTemplate.getForEntity("/envios/public/{id}", EnvioDTO.class, respuestaPOST.getBody().getId());

        Assertions.assertThat(respuestaGET.getBody()).isNotEmpty();
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaGET2.getBody().getEstado()).isEqualByComparingTo(Estado.Extraviado);
        Assertions.assertThat(respuestaGET.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET2.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);


    }


    @Test
    void consultaEnviosExtraviados() {

        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST5 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST5 Marin - Ballena 66668 - Avenida Esofago 123");

        EnvioDTO envio2 = new EnvioDTO(
                "Madrid",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST6 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST6 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/envios/private/nuevoenvio", envio, EnvioDTO.class);
        ResponseEntity<EnvioDTO> respuestaPOST2 = restTemplate.postForEntity("/envios/private/nuevoenvio", envio2, EnvioDTO.class);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            restTemplate.exchange("/envios/private/siguientepunto", HttpMethod.PUT, null, Void.class);


        }

        EnvioDTO envio3 = new EnvioDTO(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "GepetoREST7 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST7 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST3 = restTemplate.postForEntity("/envios/private/nuevoenvio", envio3, EnvioDTO.class);
        ResponseEntity respuestaPUT = restTemplate.exchange("/envios/private/testextraviados?_fecha=2021-12-31T00:00:00", HttpMethod.PUT, null, Void.class);
        String aux = LocalDateTime.now().minus(1, ChronoUnit.DAYS).toString();
        String aux2 = LocalDateTime.now().toString();
        ResponseEntity<List> respuestaGET1 = restTemplate.getForEntity("/envios/private/extraviados?desdeFecha=" + aux + "&hastaFecha=" + aux2, List.class);
        ResponseEntity<List> respuestaGET2 = restTemplate.getForEntity("/envios/private/extraviados?desdeFecha=1900-12-31T00:00:00&hastaFecha=1901-12-31T00:00:00", List.class);
        ResponseEntity<List> respuestaGET3 = restTemplate.getForEntity("/envios/private/extraviados", List.class);


        Assertions.assertThat(respuestaGET1.getBody().size()).isEqualTo(2);
        Assertions.assertThat(respuestaGET2.getBody().size()).isEqualTo(0);
        Assertions.assertThat(respuestaGET3.getBody().size()).isEqualTo(2);
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaPOST2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaPOST3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET1.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET2.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET3.getStatusCode()).isEqualTo(HttpStatus.OK);


    }

    @Test
    void consultaPorcentajeEnviosExtraviados() {

        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST8 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST8 Marin - Ballena 66668 - Avenida Esofago 123");

        EnvioDTO envio2 = new EnvioDTO(
                "Madrid",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST9 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST9 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/envios/private/nuevoenvio", envio, EnvioDTO.class);
        ResponseEntity<EnvioDTO> respuestaPOST2 = restTemplate.postForEntity("/envios/private/nuevoenvio", envio2, EnvioDTO.class);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            restTemplate.exchange("/envios/private/siguientepunto", HttpMethod.PUT, null, Void.class);


        }

        EnvioDTO envio3 = new EnvioDTO(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "GepetoREST10 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST10 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST3 = restTemplate.postForEntity("/envios/private/nuevoenvio", envio3, EnvioDTO.class);
        ResponseEntity respuestaPUT = restTemplate.exchange("/envios/private/testextraviados?_fecha=2021-12-31T00:00:00", HttpMethod.PUT, null, Void.class);

        ResponseEntity<Double> respuestaGET1 = restTemplate.getForEntity("/envios/private/extraviados/porcentaje?ultimo=dia", Double.class);

        Assertions.assertThat(respuestaGET1.getBody()).isCloseTo(66.66, within(0.1)); //Si no se borra la base de datos antes de este test, fallara porque esta hecho para que sean 2/3 extraviados
        Assertions.assertThat(respuestaGET1.getBody()).isPositive();
        Assertions.assertThat(respuestaGET1.getBody()).isLessThan(100);
        Assertions.assertThat(respuestaGET1.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaPOST2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaPOST3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


    @BeforeEach
    void limpiarBaseDatos() {
        limpiadorBaseDatos.limpiar();
    }


}