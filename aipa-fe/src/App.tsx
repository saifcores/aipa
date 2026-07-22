import { useCallback, useEffect, useState } from "react";
import { askAssistant, listTransactions } from "./lib/api";
import type { ChatMessage, Transaction } from "./lib/types";
import { AppShell } from "./components/AppShell";
import { ChatPanel } from "./components/ChatPanel";
import { TransactionLedger } from "./components/TransactionLedger";

function createId() {
  return crypto.randomUUID();
}

export default function App() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isThinking, setIsThinking] = useState(false);
  const [chatError, setChatError] = useState<string | null>(null);

  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [ledgerLoading, setLedgerLoading] = useState(true);
  const [ledgerError, setLedgerError] = useState<string | null>(null);

  const refreshLedger = useCallback(async () => {
    setLedgerLoading(true);
    setLedgerError(null);
    try {
      const data = await listTransactions();
      setTransactions(data);
    } catch (error) {
      setLedgerError(
        error instanceof Error
          ? error.message
          : "Impossible de charger les transactions",
      );
    } finally {
      setLedgerLoading(false);
    }
  }, []);

  useEffect(() => {
    void refreshLedger();
  }, [refreshLedger]);

  const onSend = useCallback(
    async (question: string) => {
      const userMessage: ChatMessage = {
        id: createId(),
        role: "user",
        content: question,
        createdAt: Date.now(),
      };
      setMessages((current) => [...current, userMessage]);
      setIsThinking(true);
      setChatError(null);

      try {
        const { answer } = await askAssistant(question);
        const assistantMessage: ChatMessage = {
          id: createId(),
          role: "assistant",
          content: answer,
          createdAt: Date.now(),
        };
        setMessages((current) => [...current, assistantMessage]);
        void refreshLedger();
      } catch (error) {
        setChatError(
          error instanceof Error
            ? error.message
            : "La requête assistant a échoué",
        );
      } finally {
        setIsThinking(false);
      }
    },
    [refreshLedger],
  );

  return (
    <AppShell
      side={
        <TransactionLedger
          transactions={transactions}
          isLoading={ledgerLoading}
          error={ledgerError}
          onRefresh={() => {
            void refreshLedger();
          }}
        />
      }
    >
      <ChatPanel
        messages={messages}
        isThinking={isThinking}
        error={chatError}
        onSend={(question) => {
          void onSend(question);
        }}
      />
    </AppShell>
  );
}
