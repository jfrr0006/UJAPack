package DAE.UJAPack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "servicios")
public class UjaPackApplication {

    public static void main(String[] args) throws Exception {

        SpringApplication servidor = new SpringApplication(UjaPackApplication.class);
        ApplicationContext context = servidor.run(args);


    }

}
