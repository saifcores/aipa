# AIPA Frontend

React · TypeScript · Tailwind CSS console for payment operations.

## Design

**Atlantic Ledger** — light ops desk with Syne + Figtree, teal signal accents,
atmospheric grid, and a chat-first composition beside a live transaction ledger.

## Run

```bash
# terminal 1 — backend
cd aipa-be
export AIPA_API_KEY=dev-api-key-change-me
mvn spring-boot:run

# terminal 2 — frontend
cd aipa-fe
cp .env.example .env   # if needed
npm install
npm run dev
```

Open http://localhost:5173

Vite proxies `/api/*` → `http://localhost:8080/*`.

## Scripts

| Command           | Description              |
| ----------------- | ------------------------ |
| `npm run dev`     | Dev server on :5173      |
| `npm run build`   | Production build         |
| `npm run preview` | Preview production build |

## Env

| Variable            | Default                 | Meaning                           |
| ------------------- | ----------------------- | --------------------------------- |
| `VITE_API_BASE_URL` | `/api`                  | API base (proxy in dev)           |
| `VITE_AIPA_API_KEY` | `dev-api-key-change-me` | Must match backend `AIPA_API_KEY` |
