package DAE.UJAPack;

import entidades.RedUjaPack;

import java.io.IOException;

//@SpringBootApplication(scanBasePackages="servicios")
public class UjaPackApplication {

	public static void main(String[] args) throws IOException {


		RedUjaPack prueba= new RedUjaPack();
		prueba.leerJson("UJAPack\\src\\main\\resources\\redujapack.json");
		prueba.pruebaVerRuta();
	//	SpringApplication servidor = new SpringApplication(UjaPackApplication.class);
	//	ApplicationContext context = servidor.run(args);


	}

}
