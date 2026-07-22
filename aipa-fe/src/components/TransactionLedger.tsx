import { motion } from "framer-motion";
import { RefreshCw } from "lucide-react";
import type { Transaction } from "../lib/types";
import { formatAmount, formatWhen } from "../lib/format";
import { StatusBadge } from "./StatusBadge";

export function TransactionLedger({
  transactions,
  isLoading,
  error,
  onRefresh,
}: {
  transactions: Transaction[];
  isLoading: boolean;
  error: string | null;
  onRefresh: () => void;
}) {
  return (
    <aside className="flex h-full min-h-0 flex-col border-l border-line bg-white/45 backdrop-blur-md">
      <div className="flex items-center justify-between border-b border-line px-5 py-4">
        <div>
          <p className="font-display text-[11px] font-semibold uppercase tracking-[0.16em] text-mist">
            Ledger
          </p>
          <h2 className="mt-1 font-display text-lg font-bold text-ink">
            Transactions
          </h2>
        </div>
        <button
          type="button"
          onClick={onRefresh}
          className="inline-flex h-9 w-9 items-center justify-center rounded-lg border border-line bg-white/80 text-slate transition hover:text-ink"
          aria-label="Rafraîchir"
        >
          <RefreshCw className={`h-4 w-4 ${isLoading ? "animate-spin" : ""}`} />
        </button>
      </div>

      <div className="min-h-0 flex-1 overflow-y-auto px-3 py-3">
        {error && (
          <p className="mb-3 rounded-lg bg-coral/5 px-3 py-2 text-sm text-coral">
            {error}
          </p>
        )}
        {isLoading && transactions.length === 0 && (
          <p className="px-2 py-6 text-sm text-mist">Chargement du ledger…</p>
        )}
        <ul className="space-y-2">
          {transactions.map((tx, index) => (
            <motion.li
              key={tx.id}
              initial={{ opacity: 0, x: 12 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: Math.min(index * 0.03, 0.3), duration: 0.3 }}
              className="rounded-xl border border-transparent px-3 py-3 transition hover:border-line hover:bg-white/80"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="min-w-0">
                  <p className="truncate font-semibold text-ink">
                    {tx.reference}
                  </p>
                  <p className="mt-0.5 truncate text-sm text-slate">
                    {tx.providerLabel}
                  </p>
                </div>
                <StatusBadge status={tx.status} />
              </div>
              <div className="mt-3 flex items-end justify-between gap-2">
                <p className="text-sm font-semibold tabular-nums text-ink">
                  {formatAmount(tx.amount, tx.currency)}
                </p>
                <p className="text-xs text-mist">{formatWhen(tx.createdAt)}</p>
              </div>
              {tx.errorCode && (
                <p className="mt-2 text-xs font-medium text-coral">
                  {tx.errorCode}
                </p>
              )}
            </motion.li>
          ))}
        </ul>
      </div>
    </aside>
  );
}
