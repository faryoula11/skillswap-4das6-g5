# Config Server (Configuration Centralisée)

Serveur de configuration centralisée pour l'ensemble des microservices de **SkillSwap** (Spring Cloud Config).

- **Port** : `8888`
- **Santé Actuator** : `http://localhost:8888/actuator/health`
- **Responsable** : Amal Jemli (`Amal.Jemli@esprit.tn`)

---

## 📌 Rôle dans l'Architecture

Le Config Server stocke et distribue les paramètres de configuration de tous les microservices à partir d'un point unique.
Il permet de :
1. Centraliser les paramètres communs (adresses Eureka, base de données, RabbitMQ).
2. Modifier la configuration d'un service sans devoir recompiler son code source.
3. Gérer différents profils d'exécution (`default`, `mysql`, `docker`).

---

## 🚀 Démarrer le Config Server

Depuis ce dossier :

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

---

## 🔍 Tester la Récupération des Configurations (HTTP)

Une fois démarré, vous pouvez vérifier dans votre navigateur ou via curl que le serveur sert bien les configurations des microservices :

- Configuration de **skill-service** : [http://localhost:8888/skill-service/default](http://localhost:8888/skill-service/default)
- Configuration de **user-service** : [http://localhost:8888/user-service/default](http://localhost:8888/user-service/default)
- Configuration de **exchange-service** : [http://localhost:8888/exchange-service/default](http://localhost:8888/exchange-service/default)
- Configuration de l'**API Gateway** : [http://localhost:8888/api-gateway/default](http://localhost:8888/api-gateway/default)

---

## 📁 Configurations Incluses dans `src/main/resources/config/`

- `application.yml` : Paramètres partagés par tous les services (Eureka, Actuator).
- `api-gateway.yml` : Routes dynamiques de l'API Gateway vers les microservices.
- `user-service.yml` : Port 8081, H2, Swagger.
- `skill-service.yml` : Port 8082, MySQL/H2, Swagger.
- `exchange-service.yml` : Port 8083, PostgreSQL, Swagger.
- `credit-service.yml` : Port 8084, MySQL credit_db, Swagger.
- `review-service.yml` : Port 8085, H2, Swagger.
- `notification-service.yml` : Port 8086, MySQL notification_db, Swagger.
- `recommendation-service.yml` : Port 8087, Neo4j, Swagger.

---

## 🔌 Comment un Microservice se Connecte au Config Server

Dans le fichier `src/main/resources/application.yml` du microservice :

```yaml
spring:
  config:
    import: "optional:configserver:http://localhost:8888"
```

Et dans son `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

---

## 🧪 Tests

```bash
mvnw.cmd test
```
