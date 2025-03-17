package app.scit46.ufc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UfcApplication {

	public static void main(String[] args) {
		SpringApplication.run(UfcApplication.class, args);
	}

}
