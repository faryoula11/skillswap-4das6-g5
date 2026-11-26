package com.skillswap.eurekaserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
class EurekaServerApplicationTests {

    @Test
    @DisplayName("Le contexte du serveur Eureka doit démarrer sans erreur")
    void contextLoads() {
    }
}
