package com.skillswap.configserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false"})
class ConfigServerApplicationTests {

    @Test
    @DisplayName("Le contexte du serveur de configuration doit démarrer sans erreur")
    void contextLoads() {
    }
}
