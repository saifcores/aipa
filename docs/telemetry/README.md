# OpenTelemetry / Jaeger captures

After running the stack with Jaeger + OTel Collector:

1. `docker compose -f docker/docker-compose.yml up -d`
2. Call `POST /ai/chat` a few times
3. Open http://localhost:16686
4. Search service `payment-ai-assistant`

Expected span hierarchy:

```
http post /ai/chat
  └─ aipa.ai.chat
       └─ (tool / JDBC observations)
```

Add screenshots here for interview demos:

- `docs/telemetry/jaeger-trace.png`
- `docs/telemetry/jaeger-ai-chat.png`
