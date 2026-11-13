package tn.esprit.test.reviewservices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reviewMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Review Microservice API")
                        .version("1.0")
                        .description(
                                "Microservice responsable de la gestion des avis et évaluations des utilisateurs."
                        )
                );
    }
}