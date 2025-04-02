package com.ads05imepac.publicacoes;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info( title = "API de Publicações de Artigos",
                                version = "1.0",
                                description = "API desenvolvida por Raphael R. Pereira"))
@SpringBootApplication
public class PublicacoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublicacoesApplication.class, args);
    }

}
