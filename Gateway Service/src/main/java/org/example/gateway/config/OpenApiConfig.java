package org.example.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EscobarTeam Festival Management API")
                        .version("1.0.0")
                        .description("API Gateway pentru microserviciile Festival Management")
                        .contact(new Contact()
                                .name("EscobarTeam")
                                .email("support@escobarteam.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8072").description("Gateway Security (OAuth2)"),
                        new Server().url("http://localhost:8073").description("Gateway API (Postman)")
                ));
    }
}
