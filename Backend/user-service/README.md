# user-service (MS1)

Microservice des **profils des membres** de SkillSwap (nom, ville, bio, langues).
L'authentification reste dans Keycloak : aucun mot de passe n'est stocké ici.

| | |
|---|---|
| Nom Eureka | `USER-SERVICE` |
| Port | 8081 |
| Base de données | H2 privée (`jdbc:h2:mem:userdb`) |
| Messagerie | RabbitMQ (producteur : `user.registered`, `user.deleted`) |
| Accès | via l'API Gateway : `/api/users/**` |
| Technologies | Java 17 · Spring Boot 3.3.5 · Spring Cloud 2023.0.3 · Spring Data JPA · Bean Validation · springdoc 2.6.0 · Maven |

## Structure
```
src/main/java/tn/esprit/skillswap/user/
├── controller/   # UserController
├── service/      # UserService
├── repository/   # UserRepository
├── model/        # User (entité)
├── dto/          # UserRequest, UserResponse
├── event/        # UserEventPublisher, UserRegistered, UserDeleted
├── exception/    # erreurs métier, Problem Details
└── config/       # configuration (RabbitMQ, sécurité)
```

## Lancement
Prérequis : JDK 17, Maven 3.9+.

```bash
mvn test
mvn spring-boot:run
```

- Swagger UI : http://localhost:8081/swagger-ui.html
- Console H2 : http://localhost:8081/h2-console (JDBC `jdbc:h2:mem:userdb`, utilisateur `sa`, mot de passe vide)
- Santé : http://localhost:8081/actuator/health

Variables d'environnement : `EUREKA_ENABLED` (défaut `false`), `EUREKA_URL`, `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD`.

## État d'avancement
Projet créé (validation 1). Logique métier à venir : CRUD (S5), événements RabbitMQ (S7), sécurité Keycloak (S8).
