# Design notes — AIPA

## Choices

| Topic        | Decision                                             |
| ------------ | ---------------------------------------------------- |
| AI framework | LangChain4j 1.18 (not Spring AI) — matches CDC       |
| Java         | 26 (Temurin)                                         |
| Spring Boot  | 3.5.16                                               |
| Architecture | Lightweight Clean Architecture packages              |
| LLM offline  | Deterministic stub ChatModel (`AIPA_AI_STUB=true`)   |
| LLM live     | Manual `OpenAiChatModel` bean (`AIPA_AI_STUB=false`) |
| Writes       | None — read-only MVP                                 |
| Security     | API Key header; OAuth2 later                         |

## Why a stub ChatModel?

Interview demos and CI must run without an OpenAI key.
The stub answers CDC scenarios by calling the same `PaymentTools`
used by the live agent, so the data path stays realistic.
