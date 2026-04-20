```markdown
# PharmaCore

Système de gestion pharmaceutique basé sur une architecture microservices.
Projet d'apprentissage couvrant Spring Boot, Spring Cloud, WebFlux, Kafka, Kubernetes et l'observabilité.

## Architecture

```
client
└── api-gateway          (Spring Cloud Gateway · WebFlux)
├── auth-service   (Spring Security · JWT · OAuth2)
├── drug-service   (médicaments, lots, DCI)          ← phase 1 ✅
├── patient-service (dossiers, historique)
└── prescription-service (ordonnances, stock)
└── Kafka event bus
└── notification-service (alertes temps réel)
```

## Stack technique

| Couche | Technologie |
|---|---|
| Langage | Java 21 |
| Framework | Spring Boot 3.4, Spring Cloud 2024 |
| Réactivité | Spring WebFlux, Project Reactor |
| Sécurité | Spring Security, JWT, OAuth2 |
| Messaging | Apache Kafka, RabbitMQ |
| Base de données | PostgreSQL, MongoDB |
| Tests | JUnit 5, Mockito |
| DevOps | Docker, Kubernetes, GitLab CI/CD |
| Observabilité | Prometheus, Grafana, ELK Stack |

## Prérequis

- Java 21 (Temurin recommandé)
- Maven 3.9+
- Docker & Docker Compose

## Démarrage rapide

```bash
# Cloner le repo
git clone https://github.com/<ton-username>/pharmacore.git
cd pharmacore

# Démarrer l'infrastructure (PostgreSQL)
docker compose up -d

# Compiler
mvn clean install -DskipTests

# Lancer drug-service
mvn -pl drug-service spring-boot:run
```

## Services

### drug-service (port 8081)

Gestion des médicaments : création, consultation, gestion des seuils de stock.

**Endpoints**

| Méthode | URL | Description |
|---|---|---|
| `POST` | `/api/v1/drugs` | Créer un médicament |
| `GET` | `/api/v1/drugs` | Lister tous les médicaments |
| `GET` | `/api/v1/drugs/{id}` | Récupérer un médicament par ID |

**Exemple de requête**

```bash
curl -X POST http://localhost:8081/api/v1/drugs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Paracétamol",
    "dci": "paracetamol",
    "category": "ANALGESIQUE",
    "unit": "comprimé",
    "minStockThreshold": 100
  }'
```

**Codes de réponse**

| Code | Signification |
|---|---|
| `201` | Médicament créé |
| `400` | Données invalides |
| `404` | Médicament introuvable |
| `409` | DCI déjà existante |

### Architecture hexagonale

```
domain/
  model/        ← Drug, DrugId, DrugCategory  (POJOs purs, zéro annotation Spring)
  exception/    ← DrugNotFoundException, DuplicateDciException
  port/
    in/         ← CreateDrugUseCase, GetDrugUseCase
    out/        ← DrugRepositoryPort

application/
  service/      ← DrugApplicationService

infrastructure/
  adapter/
    in/rest/    ← DrugController, DTOs, GlobalExceptionHandler
    out/persistence/ ← DrugJpaEntity, DrugPersistenceAdapter
  config/       ← BeanConfig
```

## Roadmap

- [x] Phase 1 — drug-service (hexagonal, PostgreSQL, JUnit/Mockito)
- [ ] Phase 2 — api-gateway WebFlux + notification-service réactif
- [ ] Phase 3 — Kafka, RabbitMQ, MongoDB
- [ ] Phase 4 — Kubernetes, Prometheus/Grafana, ELK, GitLab CI/CD

## Variables d'environnement

| Variable | Défaut | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/pharmacore_drug` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `pharmacore` | Utilisateur DB |
| `SPRING_DATASOURCE_PASSWORD` | `pharmacore` | Mot de passe DB |
| `SERVER_PORT` | `8081` | Port du service |
```

---

Maintenant push :

```bash
cd pharmacore
git init
git add .
git commit -m "feat: phase 1 - drug-service avec architecture hexagonale"
git branch -M develop
git remote add origin https://github.com/devProgOussou/pharmacore.git
git push -u origin develop
```
