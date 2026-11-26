# skill-service (MS2)

Microservice souverain de SkillSwap responsable de la gestion du catalogue officiel et des compétences déclarées par chaque membre.

- **Port** : `8082`
- **Nom dans Eureka** : `SKILL-SERVICE`
- **Swagger UI** : `http://localhost:8082/swagger-ui.html`
- **Santé & Observabilité Actuator** : `http://localhost:8082/actuator/health`, `/actuator/info`, `/actuator/metrics`
- **Base de données** : MySQL `skill_db` (`localhost:3306`)
- **Responsable** : Amal Jemli (`Amal.Jemli@esprit.tn`)

---

## 📌 Architecture et Fonctionnalités

### 1. Modèle de Données (Database-per-Service)
- **`Category`** : Catégories du catalogue (`id`, `name` unique, `description`).
- **`Skill`** : Compétences du catalogue rattachées à une catégorie (`id`, `name` unique, `description`, `category_id`).
- **`UserSkill`** : Association membre-compétence (`id`, `user_id` [sub Keycloak], `skill_id`, `type` [OFFERED/WANTED], `level` [BEGINNER/INTERMEDIATE/EXPERT], `description`, `active`).
  - **Contrainte d'intégrité** : Unicité sur le triplet `(user_id, skill_id, type)`.
  - **Découplage** : Le `user_id` est un identifiant logique texte sans clé étrangère SQL.

### 2. Suppression Logique & Réactivation (Soft Delete)
- `DELETE /api/user-skills/{id}` désactive la compétence (`active = false`) sans supprimer la ligne en base, protégeant ainsi l'historique des échanges dans `exchange-service`.
- Si un membre redéclare ultérieurement la même compétence, l'enregistrement existant est **automatiquement réactivé** et mis à jour, évitant les violations de contrainte d'unicité.

### 3. Gestion Normalisée des Erreurs (RFC 7807 Problem Details)
Toutes les erreurs renvoient une structure standard avec `type`, `title`, `status`, `detail`, `instance`, `timestamp` et une table `errors` détaillée par champ en cas d'erreur de validation.

---

## 📡 Endpoints REST

### Catégories (`/api/categories`)
| Méthode | Endpoint | Description | Rôle requis |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Lister toutes les catégories avec le nombre de compétences | Public |
| `GET` | `/api/categories/{id}` | Consulter une catégorie par son ID | Public |
| `POST` | `/api/categories` | Ajouter une catégorie (unicité du nom) | `ADMIN` |
| `PUT` | `/api/categories/{id}` | Modifier une catégorie | `ADMIN` |
| `DELETE` | `/api/categories/{id}` | Supprimer une catégorie (refusé si non vide) | `ADMIN` |

### Catalogue des Compétences (`/api/skills`)
| Méthode | Endpoint | Description | Rôle requis |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/skills?categoryId=&q=&page=&size=&sort=` | Recherche paginée par mot-clé et catégorie | Public |
| `GET` | `/api/skills/{id}` | Consulter les détails d'une compétence | Public |
| `POST` | `/api/skills` | Ajouter une compétence au catalogue | `ADMIN` |
| `PUT` | `/api/skills/{id}` | Modifier une compétence | `ADMIN` |
| `DELETE` | `/api/skills/{id}` | Supprimer une compétence du catalogue | `ADMIN` |
| `GET` | `/api/skills/stats` | Statistiques globales (offertes vs recherchées, par catégorie) | `ADMIN` |

### Compétences des Membres (`/api/user-skills`)
| Méthode | Endpoint | Description | Rôle requis |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/user-skills` | Déclarer ou réactiver une compétence | `USER` (`X-User-Id`) |
| `GET` | `/api/user-skills/me` | Consulter mes compétences actives | `USER` (`X-User-Id`) |
| `GET` | `/api/user-skills/{id}` | **Endpoint OpenFeign** : vérification par `exchange-service` | Public / Inter-services |
| `GET` | `/api/user-skills?userId=&skillId=&type=&page=&size=` | Recherche paginée (ex: qui enseigne telle compétence ?) | Public |
| `PUT` | `/api/user-skills/{id}` | Modifier niveau ou description | Propriétaire uniquement |
| `DELETE` | `/api/user-skills/{id}` | Désactiver une compétence (Soft delete) | Propriétaire ou `ADMIN` |

---

## 🔐 Authentification & Tests Swagger

En phase de développement (avant l'activation du Keycloak mutualisé en séance 8) :
1. Ouvrez `http://localhost:8082/swagger-ui.html`.
2. Cliquez sur le bouton vert **Authorize**.
3. Renseignez :
   - `X-User-Id` : votre identifiant (ex: `user-123`).
   - `X-User-Roles` : vos rôles (ex: `USER` ou `USER,ADMIN`).

---

## 🧪 Tests Automatisés

Le projet inclut une suite complète de tests d'intégration avec Spring MockMvc :
```bash
./mvnw test        # Linux / macOS
mvnw.cmd test      # Windows
```
Résultats : **8/8 tests réussis** couvrant :
- L'initialisation du catalogue (`DataInitializer`).
- La recherche paginée par mot-clé et par catégorie.
- Le contrôle d'accès RBAC (403 Forbidden pour un non-admin).
- La gestion des conflits (409 Conflict sur les doublons).
- Le cycle de vie complet `UserSkill` : déclaration, doublon 409, consultation `/me`, vérification OpenFeign, soft delete (204) et réactivation avec le même identifiant.
- Le calcul des statistiques pour l'administrateur.

---

## 🚀 Démarrage

1. Démarrer MySQL (XAMPP / Wamp / MySQL Service sur le port `3306`). La base `skill_db` est créée automatiquement.
2. Lancer `eureka-server` (port `8761`) et `config-server` (port `8888`).
3. Démarrer `skill-service` :

```bash
mvnw.cmd spring-boot:run
```
