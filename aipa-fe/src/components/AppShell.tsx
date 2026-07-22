import { motion } from "framer-motion";
import { Activity, MessageSquareText } from "lucide-react";
import type { ReactNode } from "react";

export function AppShell({
  children,
  side,
}: {
  children: ReactNode;
  side: ReactNode;
}) {
  return (
    <div className="relative min-h-full bg-ops-atmosphere">
      <div className="pointer-events-none absolute inset-0 bg-ops-grid opacity-70" />

      <div className="relative mx-auto flex h-full max-w-[1400px] flex-col px-4 py-4 md:px-6 md:py-5">
        <motion.header
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45 }}
          className="mb-4 flex items-center justify-between gap-4"
        >
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-ink text-sea-bright">
              <Activity className="h-5 w-5" strokeWidth={2.25} />
            </div>
            <div>
              <p className="font-display text-xl font-bold tracking-tight text-ink">
                AIPA
              </p>
              <p className="text-xs text-mist">Payment Operations Desk</p>
            </div>
          </div>

          <div className="hidden items-center gap-2 rounded-full border border-line bg-white/60 px-3 py-1.5 text-xs text-slate backdrop-blur sm:flex">
            <MessageSquareText className="h-3.5 w-3.5 text-sea" />
            Lecture seule · API locale
          </div>
        </motion.header>

        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.08 }}
          className="grid min-h-0 flex-1 overflow-hidden rounded-[1.5rem] border border-line bg-white/35 shadow-[0_30px_80px_-40px_rgba(15,28,36,0.45)] backdrop-blur-xl lg:grid-cols-[minmax(0,1.4fr)_minmax(300px,0.85fr)]"
        >
          <main className="flex min-h-0 flex-col p-4 md:p-6">{children}</main>
          <div className="hidden min-h-0 lg:block">{side}</div>
        </motion.div>

        <div className="mt-4 max-h-[40vh] overflow-hidden rounded-[1.5rem] border border-line bg-white/45 backdrop-blur-md lg:hidden">
          {side}
        </div>
      </div>
    </div>
  );
}
