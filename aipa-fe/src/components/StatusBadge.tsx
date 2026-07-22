import type { TransactionStatus } from "../lib/types";
import { statusTone } from "../lib/format";

const styles = {
  success: "bg-sea/10 text-sea ring-sea/20",
  danger: "bg-coral/10 text-coral ring-coral/20",
  warn: "bg-amber/10 text-amber ring-amber/20",
  neutral: "bg-ink/5 text-slate ring-ink/10",
} as const;

export function StatusBadge({ status }: { status: TransactionStatus }) {
  const tone = statusTone(status);
  return (
    <span
      className={`inline-flex items-center rounded-md px-2 py-0.5 text-[11px] font-semibold tracking-wide ring-1 ring-inset ${styles[tone]}`}
    >
      {status}
    </span>
  );
}
