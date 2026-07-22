import { ArrowUp } from "lucide-react";
import { useState, type FormEvent, type KeyboardEvent } from "react";

export function Composer({
  onSend,
  disabled,
}: {
  onSend: (question: string) => void;
  disabled?: boolean;
}) {
  const [value, setValue] = useState("");

  function submit(event?: FormEvent) {
    event?.preventDefault();
    const question = value.trim();
    if (!question || disabled) return;
    onSend(question);
    setValue("");
  }

  function onKeyDown(event: KeyboardEvent<HTMLTextAreaElement>) {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      submit();
    }
  }

  return (
    <form
      onSubmit={submit}
      className="rounded-2xl border border-line bg-white/80 p-3 shadow-[0_12px_40px_-24px_rgba(15,28,36,0.45)] backdrop-blur-md"
    >
      <label htmlFor="aipa-question" className="sr-only">
        Question à AIPA
      </label>
      <textarea
        id="aipa-question"
        rows={2}
        value={value}
        disabled={disabled}
        onChange={(event) => setValue(event.target.value)}
        onKeyDown={onKeyDown}
        placeholder="Demandez le statut d’un paiement, un code d’échec, un filtre opérateur…"
        className="w-full resize-none bg-transparent px-2 py-1 text-[15px] leading-relaxed text-ink outline-none placeholder:text-mist disabled:opacity-60"
      />
      <div className="mt-2 flex items-center justify-between gap-3 px-1">
        <p className="text-xs text-mist">
          Entrée pour envoyer · Shift+Entrée pour une nouvelle ligne
        </p>
        <button
          type="submit"
          disabled={disabled || !value.trim()}
          className="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-ink text-foam transition hover:bg-ink-soft disabled:cursor-not-allowed disabled:bg-mist"
          aria-label="Envoyer"
        >
          <ArrowUp className="h-4 w-4" strokeWidth={2.5} />
        </button>
      </div>
    </form>
  );
}
