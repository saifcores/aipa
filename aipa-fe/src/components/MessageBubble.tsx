import { motion } from "framer-motion";
import type { ChatMessage } from "../lib/types";

export function MessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === "user";

  return (
    <motion.article
      layout
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35, ease: [0.22, 1, 0.36, 1] }}
      className={`flex ${isUser ? "justify-end" : "justify-start"}`}
    >
      <div
        className={`max-w-[min(100%,42rem)] rounded-2xl px-4 py-3 text-[15px] leading-relaxed ${
          isUser
            ? "bg-ink text-foam"
            : "border border-line bg-white/85 text-ink shadow-[0_8px_30px_-20px_rgba(15,28,36,0.35)] backdrop-blur"
        }`}
      >
        {!isUser && (
          <p className="mb-1 font-display text-[11px] font-semibold uppercase tracking-[0.14em] text-sea">
            AIPA
          </p>
        )}
        <p className="whitespace-pre-wrap">{message.content}</p>
      </div>
    </motion.article>
  );
}

export function ThinkingIndicator() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="inline-flex items-center gap-2 rounded-2xl border border-line bg-white/80 px-4 py-3 text-sm text-slate backdrop-blur"
    >
      <span className="relative flex h-2 w-2">
        <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-sea-bright opacity-60" />
        <span className="relative inline-flex h-2 w-2 rounded-full bg-sea" />
      </span>
      Interrogation du ledger…
    </motion.div>
  );
}
