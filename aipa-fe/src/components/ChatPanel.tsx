import { useEffect, useRef } from "react";
import type { ChatMessage } from "../lib/types";
import { Composer } from "./Composer";
import { MessageBubble, ThinkingIndicator } from "./MessageBubble";
import { PromptChips } from "./PromptChips";

export function ChatPanel({
  messages,
  isThinking,
  error,
  onSend,
}: {
  messages: ChatMessage[];
  isThinking: boolean;
  error: string | null;
  onSend: (question: string) => void;
}) {
  const endRef = useRef<HTMLDivElement>(null);
  const isEmpty = messages.length === 0;

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
  }, [messages, isThinking]);

  return (
    <section className="flex min-h-0 flex-1 flex-col">
      <div className="min-h-0 flex-1 overflow-y-auto px-1 pb-4 pt-2 md:px-2">
        {isEmpty ? (
          <div className="flex h-full min-h-[18rem] flex-col justify-end gap-8 pb-2">
            <div className="max-w-2xl">
              <p className="font-display text-sm font-semibold uppercase tracking-[0.18em] text-sea">
                Assistant opérations
              </p>
              <h1 className="mt-3 font-display text-4xl font-bold tracking-tight text-ink md:text-5xl">
                AIPA
              </h1>
              <p className="mt-4 max-w-xl text-base leading-relaxed text-slate md:text-lg">
                Interrogez le ledger paiements en langage naturel — statuts,
                échecs, opérateurs et clients à risque, sans quitter le desk.
              </p>
            </div>
            <PromptChips onSelect={onSend} disabled={isThinking} />
          </div>
        ) : (
          <div className="mx-auto flex max-w-3xl flex-col gap-4">
            {messages.map((message) => (
              <MessageBubble key={message.id} message={message} />
            ))}
            {isThinking && <ThinkingIndicator />}
            <div ref={endRef} />
          </div>
        )}
      </div>

      <div className="shrink-0 space-y-3">
        {error && (
          <p
            className="rounded-xl border border-coral/20 bg-coral/5 px-4 py-3 text-sm text-coral"
            role="alert"
          >
            {error}
          </p>
        )}
        {!isEmpty && <PromptChips onSelect={onSend} disabled={isThinking} />}
        <Composer onSend={onSend} disabled={isThinking} />
      </div>
    </section>
  );
}
