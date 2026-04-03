package br.ufrn.imd.sgam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGAM API")
                        .description("Sistema Gerenciador de Apresentações Musicais - Escola de Música da UFRN")
                        .version("v1.0.0")
                        .contact(new Contact())
                );
    }
}