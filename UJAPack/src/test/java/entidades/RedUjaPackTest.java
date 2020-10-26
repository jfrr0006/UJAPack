package entidades;

import entidades.PuntoRuta.PuntoRuta;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class RedUjaPackTest {


    RedUjaPack red = new RedUjaPack();

    @Test
    void listaRutaMinima() {
        String remi1 = "Ceuta";
        String desti1 = "Barcelona";

        List<PuntoRuta> rutaMinima = red.listaRutaMinima(remi1, desti1);
        Assertions.assertThat(rutaMinima).isNotEmpty();
    }
}