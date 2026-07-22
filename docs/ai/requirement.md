# AI Payment Operations Assistant — Functional & Technical Spec

See the project root conversation / original CDC.
This file tracks the implemented MVP scope.

## Implemented

- Read-only transaction & customer model (PostgreSQL 17 + Flyway)
- REST: `GET /transactions`, `GET /transactions/{id}`, `POST /ai/chat`
- LangChain4j agent + payment tools
- API Key security (MVP)
- OpenAPI / Swagger UI
- OpenTelemetry → Collector → Jaeger
- Docker Compose (backend, Postgres, PgAdmin, Jaeger, OTel Collector)
- Unit + Testcontainers integration tests
- Postman collection + README

## Out of scope (documented evolutions)

- OAuth2 / JWT
- Write operations
- Persistent chat memory
- RAG / real PSP connectors
