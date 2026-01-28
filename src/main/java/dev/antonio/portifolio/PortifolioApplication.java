package dev.antonio.portifolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PortifolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortifolioApplication.class, args);
	}

}
