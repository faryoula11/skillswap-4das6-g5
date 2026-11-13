# SkillSwap

**Plateforme d'échange de compétences par crédits-temps**

Mini-projet du module *Applications Web Distribuées (MT-41)* · ESPRIT · Classe **4DAS6 – Groupe 5** · Année universitaire 2026-2027

> **Statut : validation intermédiaire (séance 4).** Le projet est en phase de conception : l'architecture, le découpage en microservices, les diagrammes et les contrats d'API sont définis ; le développement métier démarre après validation.

---

## 1. Le sujet

### Contexte et problématique
Beaucoup de personnes maîtrisent une compétence qu'elles pourraient transmettre et souhaitent en apprendre d'autres sans pouvoir les payer. Les cours en ligne sont payants et l'entraide entre particuliers reste informelle, sans suivi ni garantie de confiance.

> *Comment permettre aux utilisateurs de partager leurs connaissances et d'apprendre de nouvelles compétences sans paiement traditionnel ?*

### Solution : SkillSwap
Un système de **crédits-temps** : **1 heure enseignée = 1 crédit gagné**, **1 heure apprise = 1 crédit dépensé**. Aucun paiement en argent.

Parcours d'un membre :
1. publier ses compétences (proposées ou recherchées) ;
2. rechercher une compétence auprès des autres membres ;
3. demander un échange à un enseignant ;
4. l'enseignant accepte (ou refuse) ; les crédits de l'apprenant sont alors bloqués ;
5. l'échange est réalisé puis marqué comme terminé ; les crédits sont transférés à l'enseignant ;
6. l'apprenant laisse un avis sur l'enseignant.

### Acteurs
| Acteur | Rôle |
|---|---|
| Visiteur | Personne non connectée ; peut s'inscrire et se connecter |
| Membre | Gère son profil, publie et recherche des compétences, demande et gère des échanges, consulte son solde, note les enseignants, lit ses notifications et ses recommandations |
| Administrateur | Gère les catégories, modère compétences et avis, ajuste un solde, consulte les statistiques |

---

## 2. Architecture

Un **point d'entrée unique** (API Gateway), sept microservices métier et une infrastructure commune. Chaque microservice possède sa **propre base de données** ; aucun service n'accède à la base d'un autre.

```
Front-end Angular ──► API Gateway :8080 ──► MS1..MS7 (une base par service)
                         │                      │  ▲
                   Keycloak :8180 (JWT)         ▼  │ événements
                   Eureka :8761            RabbitMQ (topic skillswap.events)
                   Config Server :8888
```

### Microservices

| Service | Responsabilité | Port | Base de données | Responsable |
|---|---|---|---|---|
| MS1 `user-service` | Profils des membres (l'identité reste dans Keycloak) | 8081 | H2 | Emna Trabelsi |
| MS2 `skill-service` | Catalogue des compétences, catégories, compétences déclarées par chaque membre | 8082 | MySQL | Amal Jemli |
| MS3 `exchange-service` | Cycle de vie d'un échange : demande, acceptation, refus, annulation, fin | 8083 | PostgreSQL | Feryel Bassem |
| MS4 `credit-service` | Portefeuille de crédits : solde, blocage, transfert, remboursement | 8084 | MySQL | Molka Khlaifi |
| MS5 `review-service` | Avis (note de 1 à 5) après un échange terminé, note moyenne | 8085 | H2 | Malek Massoudi |
| MS6 `notification-service` | Notifications dans l'application et e-mails, déclenchées par les événements | 8086 | MySQL | Wiem Jlassi |
| MS7 `recommendation-service` | Service commun : graphe membres–compétences–échanges, recommandations | 8087 | Neo4j | Toute l'équipe |

### Infrastructure
| Composant | Port | Rôle |
|---|---|---|
| API Gateway (Spring Cloud Gateway) | 8080 | Point d'entrée, routage `lb://`, validation des JWT |
| Eureka | 8761 | Découverte de services |
| Config Server | 8888 | Configuration centralisée |
| Keycloak | 8180 | Authentification (OIDC, realm `skillswap`, rôles `USER` / `ADMIN`) |
| RabbitMQ | 5672 / 15672 | Bus d'événements (AMQP / console) |

### Communications
- **Synchrone** : OpenFeign avec Resilience4j (délai de 2 s, circuit breaker, réponse 503 en cas d'indisponibilité).
- **Asynchrone** : RabbitMQ, exchange topic `skillswap.events`, une file durable par consommateur, acquittement manuel, idempotence par `eventId`, 3 tentatives puis file morte (DLQ).

### Technologies
Java 17 · Spring Boot 3 · Spring Cloud (Gateway, OpenFeign, Eureka, Config) · Resilience4j · Keycloak · RabbitMQ · H2 / MySQL / PostgreSQL / Neo4j · Swagger (springdoc-openapi) · Angular · Docker / Docker Compose · Git / GitHub Projects.

---

## 3. Organisation du dépôt

```
skillswap-4das6-g5/
├── Backend/                  # un projet Maven par microservice
│   ├── user-service/
│   ├── skill-service/
│   ├── exchange-service/
│   ├── credit-service/
│   ├── review-service/
│   ├── notification-service/
│   └── recommendation-service/   (+ api-gateway, eureka, config-server)
├── Frontend/                 # application Angular
├── Documentation/
│   ├── Presentation/         # présentation de la séance 4
│   └── Diagrams/             # architecture globale, classes, séquences
└── README.md
```

- **Branches** : `main` (protégée), `develop`, `feature/<service>`.
- **Intégration** : Pull Request relue avant fusion vers `develop`.
- **Suivi des tâches** : GitHub Projects (tableau Kanban).
- **Documents** : dossier technique complet et présentation dans `Documentation/`.

---

## 4. Équipe

| Membre | Microservice | Tâche transversale |
|---|---|---|
| Emna Trabelsi | MS1 · user-service | API Gateway (routes, CORS) |
| Amal Jemli | MS2 · skill-service + coordination MS7 | Eureka et Config Server |
| Feryel Bassem | MS3 · exchange-service | Coordination du front-end Angular |
| Molka Khlaifi | MS4 · credit-service | RabbitMQ (exchange, files, DLQ) |
| Malek Massoudi | MS5 · review-service | Keycloak (realm, rôles, client) |
| Wiem Jlassi | MS6 · notification-service | Docker Compose, README, présentation |
| Toute l'équipe | MS7 · recommendation-service (commun) | Chacun ajoute le consommateur de son domaine |

---

## 5. Planning prévisionnel (S4 → S10)

| Semaine | Travaux |
|---|---|
| S4 | Conception, dépôt Git, présentation |
| S5 | Spring Boot, CRUD, Swagger ; Eureka et Config Server |
| S6 | Appels OpenFeign ; modèle Neo4j ; début Docker Compose |
| S7 | Événements RabbitMQ ; Neo4j |
| S8 | Sécurité Keycloak (JWT) ; front-end Angular |
| S9 | Front-end ; tests d'intégration |
| S10 | Docker Compose complet, finalisation et soutenance |

---

## 6. Lancement

### Un microservice seul (exemple : `user-service`)
Prérequis : JDK 17, Maven 3.9+.

```bash
cd Backend/user-service
mvn spring-boot:run
```

| Accès | URL |
|---|---|
| API | http://localhost:8081/api/users |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Console H2 | http://localhost:8081/h2-console (JDBC : `jdbc:h2:mem:userdb`, utilisateur `sa`, mot de passe vide) |

Tests : `mvn test`.

### Plateforme complète
Le lancement de l'ensemble (API Gateway, Eureka, Config Server, Keycloak, RabbitMQ, bases et microservices) se fera avec Docker Compose à partir de la semaine 6 :

```bash
docker compose up --build
```

---

## 7. Documentation

Tout est dans le dossier [`Documentation/`](Documentation/) :
- `Presentation/` : support de la séance 4 ;
- `Diagrams/` : diagramme d'architecture globale, diagrammes de classes et de séquence de chaque microservice ;
- le **dossier technique** du projet (Word / PDF) : sujet, acteurs et fonctionnalités, découpage, données, interactions, architecture, contrats d'API.
