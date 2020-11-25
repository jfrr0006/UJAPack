package DAE.UJAPack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "beans")
@EntityScan(basePackages = "entidades")
public class UjaPackApplication {

    public static void main(String[] args) throws Exception {

        //   SpringApplication servidor = new SpringApplication(UjaPackApplication.class);
        //   ApplicationContext context = servidor.run(args);
        SpringApplication.run(UjaPackApplication.class, args);

    }

}
