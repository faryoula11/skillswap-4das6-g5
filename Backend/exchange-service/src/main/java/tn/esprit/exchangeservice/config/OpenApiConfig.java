
package tn.esprit.exchangeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SkillSwap - Exchange Service API",
                version = "1.0",
                description = "API de gestion des échanges et des formations"
        ),
        security = @SecurityRequirement(name = "keycloak")
)
@SecurityScheme(
        name = "keycloak",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl =
                                "http://localhost:8180/realms/skillswap/protocol/openid-connect/auth",
                        tokenUrl =
                                "http://localhost:8180/realms/skillswap/protocol/openid-connect/token",
                        scopes = {
                                @OAuthScope(
                                        name = "openid",
                                        description = "Authentification OpenID Connect"
                                )
                        }
                )
        )
)
public class OpenApiConfig {
}
