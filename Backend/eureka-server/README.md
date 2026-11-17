# Eureka Server (Service Discovery)

Serveur de découverte et d'enregistrement des microservices de la plateforme **SkillSwap** (Spring Cloud Netflix Eureka).

- **Port** : `8761`
- **Dashboard Web** : `http://localhost:8761`
- **Santé Actuator** : `http://localhost:8761/actuator/health`
- **Responsable** : Amal Jemli (`Amal.Jemli@esprit.tn`)

---

## 📌 Rôle dans l'Architecture

Le serveur Eureka permet la **découverte dynamique** des instances de microservices sans coder les adresses IP ou ports en dur.
Chaque microservice s'enregistre à son démarrage sous son nom logique (ex: `SKILL-SERVICE`, `USER-SERVICE`, `EXCHANGE-SERVICE`).
L'**API Gateway** et les clients **OpenFeign** utilisent Eureka pour router dynamiquement les requêtes et faire de la répartition de charge (`lb://`).

---

## 🚀 Démarrer le Serveur Eureka

Depuis ce dossier :

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Une fois démarré, ouvrez votre navigateur sur [http://localhost:8761](http://localhost:8761) pour voir le tableau de bord avec tous les microservices connectés.

---

## 🔌 Configuration pour les Microservices de l'Équipe

Pour que vos microservices s'enregistrent sur Eureka, ajoutez simplement cette configuration dans leur `application.yml` :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

Et dans le `pom.xml` de votre microservice :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

---

## 🧪 Tests

```bash
mvnw.cmd test
```
