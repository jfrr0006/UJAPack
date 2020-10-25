package DAE.UJAPack;

import servicios.UjaPack;

//@SpringBootApplication(scanBasePackages="servicios")
public class UjaPackApplication {

	public static void main(String[] args) throws Exception {
		String remi1="Ceuta";
		String desti1="Barcelona";
		Float peso1 = 5.0f;
		Float dimen1 = 10.0f;
		UjaPack servicio = new UjaPack();

		servicio.generarEnvio(remi1,desti1,peso1,dimen1);
		for (int i=0;i<25;i++){

			servicio.avanzarEnvios();


		}
		servicio.avanzarEnvios();
		servicio.mostrarPrueba();
		//	SpringApplication servidor = new SpringApplication(UjaPackApplication.class);
		//	ApplicationContext context = servidor.run(args);


	}

}
