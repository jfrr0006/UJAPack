package entidades;

import entidades.PuntoRuta.CentroLog;
import entidades.PuntoRuta.PuntoRuta;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class RegistroTest {

    @Test
    void testActualizarRegistro(){
        PuntoRuta punto = new CentroLog(12345,"CentroA","OficinaA");
        Registro registro =new Registro(punto);
        registro.actualizarRegistro(LocalDateTime.now(),true);

        Assertions.assertThat(registro.getFecha()).isNotNull();
        Assertions.assertThat(registro.getEntrada()).isNotNull();


    }




}