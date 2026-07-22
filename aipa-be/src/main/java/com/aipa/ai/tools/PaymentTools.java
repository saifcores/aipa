package com.aipa.ai.tools;

import com.aipa.ai.replies.OpsReplyFormatter;
import com.aipa.application.TransactionQueryService;
import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaymentTools {

        private final TransactionQueryService transactionQueryService;
        private final OpsReplyFormatter replies;

        public PaymentTools(
                        TransactionQueryService transactionQueryService,
                        OpsReplyFormatter replies) {
                this.transactionQueryService = transactionQueryService;
                this.replies = replies;
        }

        @Tool("Find a payment transaction by its business reference (e.g. TX45892).")
        public String findTransaction(
                        @P("Transaction reference such as TX45892") String reference) {
                return transactionQueryService.findByReference(reference)
                                .map(replies::singleTransaction)
                                .orElseGet(() -> replies.transactionNotFound(reference));
        }

        @Tool("List failed payment transactions for a given ISO date (yyyy-MM-dd). "
                        + "Use today's date when the user says 'aujourd'hui' / 'today'.")
        public String findFailedTransactions(
                        @P("ISO date yyyy-MM-dd") String date) {
                LocalDate localDate = parseDate(date);
                List<PaymentTransaction> failed = transactionQueryService.findFailedOn(localDate);
                return replies.failedList(localDate, failed);
        }

        @Tool("Count transactions by status. Status must be one of: "
                        + "PENDING, SUCCESS, FAILED, CANCELLED, EXPIRED.")
        public String countTransactions(
                        @P("Transaction status") String status) {
                TransactionStatus parsed = TransactionStatus.valueOf(status.trim().toUpperCase());
                long count = transactionQueryService.countByStatus(parsed);
                return replies.statusCount(parsed, count);
        }

        @Tool("List transactions for a payment provider. Providers: "
                        + "Wave, Orange Money, Free Money, Visa, Mastercard.")
        public String findTransactionsByProvider(
                        @P("Payment provider name") String provider) {
                PaymentProvider parsed = PaymentProvider.fromDisplayName(provider);
                List<PaymentTransaction> txs = transactionQueryService.findByProvider(parsed);
                return replies.providerList(parsed.displayName(), txs, 20);
        }

        @Tool("List transactions for a provider with amount greater than or equal "
                        + "to a minimum (in the transaction currency, usually XOF/FCFA).")
        public String findTransactionsByProviderAboveAmount(
                        @P("Payment provider name") String provider,
                        @P("Minimum amount inclusive") double minAmount) {
                PaymentProvider parsed = PaymentProvider.fromDisplayName(provider);
                List<PaymentTransaction> txs = transactionQueryService.findByProviderAboveAmount(
                                parsed, BigDecimal.valueOf(minAmount));
                return replies.providerAboveAmount(
                                parsed.displayName(), minAmount, txs);
        }

        @Tool("Explain why a transaction failed using its error code catalog "
                        + "and transaction context.")
        public String explainTransactionFailure(
                        @P("Transaction reference") String reference) {
                return transactionQueryService.findByReference(reference)
                                .map(replies::failureExplanation)
                                .orElseGet(() -> replies.failureNotFound(reference));
        }

        @Tool("Return aggregate payment statistics: totals by status/provider, "
                        + "failed today, pending count.")
        public String getStatistics() {
                Map<String, Object> stats = transactionQueryService.getStatistics();
                return replies.statistics(stats);
        }

        @Tool("Find customers who had multiple FAILED payments during the last 7 days.")
        public String findCustomersWithMultipleFailures(
                        @P("Minimum number of failures, typically 2") int minFailures) {
                int min = Math.max(minFailures, 2);
                List<Map<String, Object>> customers = transactionQueryService
                                .findCustomersWithMultipleFailuresThisWeek(min);
                return replies.customersWithFailures(customers, min);
        }

        @Tool("Return today's date in ISO format (yyyy-MM-dd) for relative queries.")
        public String today() {
                return transactionQueryService.today().toString();
        }

        private LocalDate parseDate(String date) {
                try {
                        return LocalDate.parse(date.trim());
                } catch (DateTimeParseException ex) {
                        return transactionQueryService.today();
                }
        }
}
