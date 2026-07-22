import { motion } from "framer-motion";

const prompts = [
  "Où est la transaction TX45892 ?",
  "Pourquoi la transaction TX45893 a échoué ?",
  "Combien de paiements ont échoué aujourd’hui ?",
  "Montre les transactions Orange Money > 50 000 FCFA",
  "Quels clients ont eu plusieurs échecs cette semaine ?",
];

export function PromptChips({
  onSelect,
  disabled,
}: {
  onSelect: (prompt: string) => void;
  disabled?: boolean;
}) {
  return (
    <div className="flex flex-wrap gap-2">
      {prompts.map((prompt, index) => (
        <motion.button
          key={prompt}
          type="button"
          disabled={disabled}
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 + index * 0.05, duration: 0.35 }}
          onClick={() => onSelect(prompt)}
          className="max-w-full truncate rounded-lg border border-line bg-white/70 px-3 py-2 text-left text-sm text-slate shadow-[0_1px_0_rgba(15,28,36,0.04)] backdrop-blur transition hover:border-sea/30 hover:text-ink disabled:cursor-not-allowed disabled:opacity-50"
        >
          {prompt}
        </motion.button>
      ))}
    </div>
  );
}
