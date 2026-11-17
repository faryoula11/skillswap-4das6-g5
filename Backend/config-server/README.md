# Config Server (Configuration Centralisée)

Serveur de configuration centralisée pour les microservices de **SkillSwap** (Spring Cloud Config).

- **Port** : `8888`
- **Santé Actuator** : `http://localhost:8888/actuator/health`
- **Responsable** : Amal Jemli (`Amal.Jemli@esprit.tn`)

---

## 📌 Rôle dans l'Architecture

Le Config Server distribue les configurations de la plateforme à partir d'un point unique.
Suite aux ajustements de conception (l'identité des membres est directement assurée par **Keycloak** sans microservice `user-service`, et le service de notification a été retiré), le Config Server gère les 5 microservices métier et l'API Gateway :

1. **`skill-service`** (port 8082) : Catalogue et compétences des membres.
2. **`exchange-service`** (port 8083) : Cycle de vie des échanges.
3. **`credit-service`** (port 8084) : Portefeuille de crédits-temps.
4. **`review-service`** (port 8085) : Avis et réputation.
5. **`recommendation-service`** (port 8087) : Graphe Neo4j et recommandations.
6. **`api-gateway`** (port 8080) : Routage dynamique.

---

## 🚀 Démarrer le Config Server

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

---

## 🔍 Tester la Récupération des Configurations (HTTP)

- [http://localhost:8888/skill-service/default](http://localhost:8888/skill-service/default)
- [http://localhost:8888/exchange-service/default](http://localhost:8888/exchange-service/default)
- [http://localhost:8888/credit-service/default](http://localhost:8888/credit-service/default)
- [http://localhost:8888/api-gateway/default](http://localhost:8888/api-gateway/default)

---

## 🧪 Tests

```bash
mvnw.cmd test
```
