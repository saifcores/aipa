# AI Payment Operations Assistant (AIPA)

Natural-language assistant for **payment operations** teams (banks, fintechs, PSPs).

> Demo stack: **Java 26 · Spring Boot 3.5 · LangChain4j · PostgreSQL 17 · OpenTelemetry · Docker**

## Monorepo layout

```
aipa/
├── aipa-be/     Spring Boot API + LangChain4j agent
├── aipa-fe/     Frontend (UI console)
├── docs/        Spec & design notes
└── README.md
```

---

## Backend (`aipa-be`)

### Prerequisites

- Java **26**, Maven **3.9+**, Docker Compose
- Optional: `OPENAI_API_KEY` for live LLM tool-calling

Without an OpenAI key, AIPA uses a deterministic stub (`AIPA_AI_STUB=true`).

### Start infrastructure

```bash
docker compose -f aipa-be/docker/docker-compose.yml up -d postgres jaeger otel-collector pgadmin
```

| Service        | URL                                                |
| -------------- | -------------------------------------------------- |
| PostgreSQL     | `localhost:5432` (`aipa` / `aipa`)                 |
| PgAdmin        | http://localhost:5050 (`admin@aipa.dev` / `admin`) |
| Jaeger UI      | http://localhost:16686                             |
| OTel Collector | `localhost:4319` (OTLP HTTP)                       |

### Run the API

```bash
cd aipa-be
export JAVA_HOME=$(sdk home java 26.0.1-tem)   # or your JDK 26
export AIPA_API_KEY=dev-api-key-change-me
mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Full backend stack with Docker

```bash
export AIPA_API_KEY=dev-api-key-change-me
docker compose -f aipa-be/docker/docker-compose.yml up --build
```

### Tests

```bash
cd aipa-be
mvn -Dtest='!*IntegrationTest' test
mvn test   # includes Testcontainers
```

### API examples

```bash
export AIPA_API_KEY=dev-api-key-change-me

curl -s -H "X-API-Key: $AIPA_API_KEY" http://localhost:8080/transactions | jq

curl -s -X POST http://localhost:8080/ai/chat \
  -H "X-API-Key: $AIPA_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"question":"Où est la transaction TX45892 ?"}' | jq
```

Postman: [`aipa-be/postman/AIPA.postman_collection.json`](aipa-be/postman/AIPA.postman_collection.json)

Env template: [`aipa-be/.env.example`](aipa-be/.env.example)

---

## Frontend (`aipa-fe`)

React · TypeScript · Tailwind console (**Atlantic Ledger**).

```bash
cd aipa-fe
npm install
npm run dev
```

Open http://localhost:5173 — chat AIPA + ledger transactions live.
Details: [`aipa-fe/README.md`](aipa-fe/README.md).

---

## Architecture

```
Utilisateur (aipa-fe)
    │
 REST API  (/transactions, /ai/chat)
    │
 Spring Boot 3 (aipa-be)
    │
 LangChain4j Agent  (+ tools)
    │
 TransactionQueryService
    │
 PostgreSQL 17
```

---

## Features (read-only)

| Use case         | Example question                                        |
| ---------------- | ------------------------------------------------------- |
| Lookup           | `Où est la transaction TX45892 ?`                       |
| Failure analysis | `Pourquoi la transaction TX45893 a échoué ?`            |
| Filter           | `Montre les transactions Orange Money > 50 000 FCFA`    |
| Stats            | `Combien de paiements ont échoué aujourd'hui ?`         |
| Ops assist       | `Quels clients ont eu plusieurs échecs cette semaine ?` |

---

## Backend package layout

```
aipa-be/src/main/java/com/aipa
├── api            REST controllers + DTOs
├── application    Read use-cases
├── domain         Model + error catalog
├── infrastructure JPA entities / repositories
├── ai             Agent, tools, memory, stub LLM
├── config         API key, OpenAPI, clock
└── telemetry      Observation / metrics hooks
```

---

## Configuration

| Variable                      | Default                 | Meaning                     |
| ----------------------------- | ----------------------- | --------------------------- |
| `AIPA_API_KEY`                | `dev-api-key-change-me` | REST API key                |
| `AIPA_AI_STUB`                | `true`                  | Offline stub ChatModel      |
| `OPENAI_API_KEY`              | _(empty)_               | Required when stub is false |
| `OPENAI_MODEL`                | `gpt-4o-mini`           | Chat model                  |
| `DB_HOST` / `DB_PORT`         | `localhost` / `5432`    | PostgreSQL                  |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4318` | Traces                      |

---

## License

Demo / interview portfolio project — use freely with attribution.
