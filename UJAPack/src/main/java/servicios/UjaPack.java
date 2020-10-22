package servicios;

import entidades.RedUjaPack;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UjaPack {

    /*Toda la red*/
    RedUjaPack red;

    public UjaPack() throws IOException {
        red= new RedUjaPack();

    }


}
