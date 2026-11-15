package com.skillswap.skillservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_USER_ID = "X-User-Id";
    public static final String SECURITY_USER_ROLES = "X-User-Roles";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SkillSwap - Skill Service API")
                .version("1.0.0")
                .description("Microservice souverain de gestion du catalogue et des compétences des membres.\n\n" +
                             "**Authentification de développement :**\n" +
                             "Cliquez sur le bouton **Authorize** pour renseigner `X-User-Id` (ex: `user-123`) " +
                             "et `X-User-Roles` (ex: `USER` ou `USER,ADMIN`).")
                .contact(new Contact()
                    .name("Amal Jemli")
                    .email("Amal.Jemli@esprit.tn")))
            .components(new Components()
                .addSecuritySchemes(SECURITY_USER_ID, new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-User-Id")
                    .description("Identifiant de l'utilisateur (ex: user-123, sub Keycloak)"))
                .addSecuritySchemes(SECURITY_USER_ROLES, new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-User-Roles")
                    .description("Rôles utilisateur séparés par des virgules (ex: USER ou USER,ADMIN)")))
            .addSecurityItem(new SecurityRequirement()
                .addList(SECURITY_USER_ID)
                .addList(SECURITY_USER_ROLES));
    }
}
