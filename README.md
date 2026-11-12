# SkillSwap

## Présentation

SkillSwap est une plateforme web de gestion des échanges de compétences permettant aux utilisateurs de partager leurs connaissances grâce à un système de crédits virtuels.

## Architecture

Le projet repose sur une architecture distribuée composée de 7 microservices :

- User Service
- Skill Service
- Exchange Service
- Credit Service
- Review Service
- Notification Service
- Recommendation Service

## Technologies prévues

- Backend : Spring Boot / Java
- Frontend : Angular
- Bases de données : PostgreSQL et Neo4j
- API Gateway : Spring Cloud Gateway
- Communication synchrone : REST / OpenFeign
- Communication asynchrone : RabbitMQ
- Sécurité : Spring Security / JWT
- Déploiement : Docker Compose

## Organisation

Le projet est réalisé par une équipe de six étudiants. Chaque membre est responsable d'un microservice individuel. Recommendation Service est développé collectivement.

## Documentation

Le dossier Documentation contient les diagrammes UML, l'architecture globale, la présentation et le dossier technique du projet.
