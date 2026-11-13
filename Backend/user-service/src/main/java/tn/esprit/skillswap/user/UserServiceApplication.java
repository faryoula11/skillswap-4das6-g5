package tn.esprit.skillswap.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MS1 - user-service : gestion des profils des membres SkillSwap.
 * L'authentification reste dans Keycloak ; ce service ne stocke aucun mot de passe.
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
