package cm.beautysempire.institut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class InstitutApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstitutApplication.class, args);
	}

}
