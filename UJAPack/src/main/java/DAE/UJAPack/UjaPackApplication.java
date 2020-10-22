package DAE.UJAPack;

import entidades.RedUjaPack;

import java.io.IOException;

//@SpringBootApplication(scanBasePackages="servicios")
public class UjaPackApplication {

	public static void main(String[] args) throws IOException {

		//Claramente esto no es asi lo uso de main para hacer pruebas hasta tener los tests

		RedUjaPack prueba= new RedUjaPack();
		prueba.pruebaVerRuta("Ceuta","Barcelona");
	//	SpringApplication servidor = new SpringApplication(UjaPackApplication.class);
	//	ApplicationContext context = servidor.run(args);


	}

}
