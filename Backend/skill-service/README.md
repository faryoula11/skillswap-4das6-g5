# skill-service

Microservice de SkillSwap qui gère le catalogue des compétences et les compétences déclarées par les membres.

- Port : 8082
- Nom dans Eureka : SKILL-SERVICE
- Swagger : http://localhost:8082/swagger-ui.html
- Santé : http://localhost:8082/actuator/health

## Lancer

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

Le service démarre avec une base H2 en mémoire. Pour utiliser MySQL : `./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql`.
