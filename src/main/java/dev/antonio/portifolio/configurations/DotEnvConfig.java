package dev.antonio.portifolio.configurations;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {


    @PostConstruct
    public void init() {

        // Carrega o arquivo .env
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // Tente usar "./" se o arquivo estiver na raiz do projeto
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

            dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}