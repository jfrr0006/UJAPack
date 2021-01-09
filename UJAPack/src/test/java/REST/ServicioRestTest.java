package REST;

import DAE.UJAPack.UjaPackApplication;
import DTO.EnvioDTO;
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


        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/private/envios/envio", envio, EnvioDTO.class);

        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);

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
        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/private/envios/envio", envio, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            ResponseEntity respuestaPUT = restTemplate.exchange("/private/envios/{id}/", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());
            Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);

        }
        ResponseEntity<String> respuestaGETactual = restTemplate.getForEntity("/public/envios/{id}/actual", String.class, respuestaPOST.getBody().getId());
        Assertions.assertThat(respuestaGETactual.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(!respuestaGETactual.getBody().isBlank());

        ResponseEntity<List> respuestaGETruta = restTemplate.getForEntity("/public/envios/{id}/ruta", List.class, respuestaPOST.getBody().getId());
        Assertions.assertThat(respuestaGETruta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGETruta.getBody()).isNotEmpty();


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


        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/private/envios/envio", envio, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);

        for (int i = 0; i < 5; i++) {
            ResponseEntity respuestaPUT = restTemplate.exchange("/private/envios/{id}/", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());
            Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);

        }
        ResponseEntity respuestaPUT2 = restTemplate.exchange("/private/envios/testextraviados", HttpMethod.PUT, null, Void.class);
        Assertions.assertThat(respuestaPUT2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List> respuestaGET = restTemplate.getForEntity("/private/envios/extraviados", List.class);
        Assertions.assertThat(respuestaGET.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET.getBody()).isNotEmpty();

        ResponseEntity<EnvioDTO> respuestaGET2 = restTemplate.getForEntity("/public/envios/{id}", EnvioDTO.class, respuestaPOST.getBody().getId());
        Assertions.assertThat(respuestaGET2.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET2.getBody().getEstado()).isEqualByComparingTo(Estado.Extraviado);


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

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/private/envios/envio", envio, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<EnvioDTO> respuestaPOST2 = restTemplate.postForEntity("/private/envios/envio", envio2, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            ResponseEntity respuestaPUT2 = restTemplate.exchange("/private/envios/", HttpMethod.PUT, null, Void.class);
            Assertions.assertThat(respuestaPUT2.getStatusCode()).isEqualTo(HttpStatus.OK);

        }

        EnvioDTO envio3 = new EnvioDTO(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "GepetoREST7 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST7 Marin - Ballena 66668 - Avenida Esofago 123");

        EnvioDTO envio4 = new EnvioDTO(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "GepetoREST7_2 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST7_2 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST3 = restTemplate.postForEntity("/private/envios/envio", envio3, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST3.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<EnvioDTO> respuestaPOST4 = restTemplate.postForEntity("/private/envios/envio", envio4, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST4.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity respuestaPUT = restTemplate.exchange("/private/envios/testextraviados", HttpMethod.PUT, null, Void.class);
        Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);

        String aux = LocalDateTime.now().minus(1, ChronoUnit.DAYS).toString();
        String aux2 = LocalDateTime.now().toString();
        ResponseEntity<List> respuestaGET1 = restTemplate.getForEntity("/private/envios/extraviados?desdeFecha=" + aux + "&hastaFecha=" + aux2, List.class);
        ResponseEntity<List> respuestaGET2 = restTemplate.getForEntity("/private/envios/extraviados?desdeFecha=1900-12-31T00:00:00&hastaFecha=1901-12-31T00:00:00", List.class);
        ResponseEntity<List> respuestaGET3 = restTemplate.getForEntity("/private/envios/extraviados", List.class);
        Assertions.assertThat(respuestaGET1.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET2.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET3.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET1.getBody().size()).isGreaterThan(0);
        Assertions.assertThat(respuestaGET2.getBody().size()).isEqualTo(0);
        Assertions.assertThat(respuestaGET3.getBody().size()).isGreaterThan(0);


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

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/private/envios/envio", envio, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<EnvioDTO> respuestaPOST2 = restTemplate.postForEntity("/private/envios/envio", envio2, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        for (int i = 0; i < 5; i++) {//Para que nos quedemos por la mitad del trayecto y ver la situacion en ese momento
            ResponseEntity respuestaPUT2 = restTemplate.exchange("/private/envios/", HttpMethod.PUT, null, Void.class);
            Assertions.assertThat(respuestaPUT2.getStatusCode()).isEqualTo(HttpStatus.OK);

        }

        EnvioDTO envio3 = new EnvioDTO(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "GepetoREST10 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST10 Marin - Ballena 66668 - Avenida Esofago 123");
        EnvioDTO envio4 = new EnvioDTO(
                "Barcelona",
                "Toledo",
                5.0f,
                10.0f,
                "GepetoREST11 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST11 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST3 = restTemplate.postForEntity("/private/envios/envio", envio3, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST3.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<EnvioDTO> respuestaPOST4 = restTemplate.postForEntity("/private/envios/envio", envio4, EnvioDTO.class);
        Assertions.assertThat(respuestaPOST4.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity respuestaPUT = restTemplate.exchange("/private/envios/testextraviados", HttpMethod.PUT, null, Void.class);
        Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Double> respuestaGET1 = restTemplate.getForEntity("/private/envios/extraviados/porcentaje?ultimo=dia", Double.class);
        Assertions.assertThat(respuestaGET1.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaGET1.getBody()).isPositive();
        Assertions.assertThat(respuestaGET1.getBody()).isLessThan(100);


    }


    @BeforeEach
    void limpiarBaseDatos() {
        limpiadorBaseDatos.limpiar();
    }


    /* Test de Notificaciones
        @Test
    void notificacionPuntoControl() {


        EnvioDTO envio = new EnvioDTO(
                "Ceuta",
                "Barcelona",
                5.0f,
                10.0f,
                "GepetoREST2 Marin - Atlantida 66667 - Calle Falsa 123",
                "PinochoREST2 Marin - Ballena 66668 - Avenida Esofago 123");

        ResponseEntity<EnvioDTO> respuestaPOST = restTemplate.postForEntity("/private/envios/envio", envio, EnvioDTO.class);


     //   ResponseEntity respuestaPUT = restTemplate.exchange("/public/envios/{id}/nuevanotificacion?notifi=Barcelona", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());

        for (int i = 0; i < 30; i++) {//Nos aseguramos que va a avanzar el envio en su totalidad
            restTemplate.exchange("/private/envios/{id}/", HttpMethod.PUT, null, Void.class, respuestaPOST.getBody().getId());

        }

        ResponseEntity<EnvioDTO> respuestaGET = restTemplate.getForEntity("/public/envios/{id}", EnvioDTO.class, respuestaPOST.getBody().getId());

        Assertions.assertThat(respuestaGET.getBody().getEstado()).isEqualByComparingTo(Estado.Entregado);//Nos aseguramos que ha sido entregado
        // Assertions.assertThat(respuestaGET.getBody().getDatos_notificacion()).contains("El envio");
        Assertions.assertThat(respuestaGET.getBody()).isNotNull();
        Assertions.assertThat(respuestaPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaGET.getStatusCode()).isEqualTo(HttpStatus.OK);
       // Assertions.assertThat(respuestaPUT.getStatusCode()).isEqualTo(HttpStatus.OK);


    }
     */
}
