package com.aipa.ai.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.aipa.ai.replies.OpsReplyFormatter;
import com.aipa.application.TransactionQueryService;
import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentToolsTest {

        @Mock
        private TransactionQueryService transactionQueryService;

        private PaymentTools tools;

        @BeforeEach
        void setUp() {
                tools = new PaymentTools(transactionQueryService, new OpsReplyFormatter());
        }

        @Test
        void findTransaction_formatsKnownReference() {
                PaymentTransaction tx = sample("TX45892", TransactionStatus.SUCCESS, null);
                when(transactionQueryService.findByReference("TX45892"))
                                .thenReturn(Optional.of(tx));

                String result = tools.findTransaction("TX45892");

                assertThat(result)
                                .contains("TX45892")
                                .contains("SUCCESS")
                                .contains("Wave")
                                .contains("25")
                                .contains("FCFA")
                                .contains("Montant");
        }

        @Test
        void findFailedTransactions_reportsEmptyDay() {
                LocalDate day = LocalDate.of(2026, 7, 22);
                when(transactionQueryService.findFailedOn(day)).thenReturn(List.of());

                assertThat(tools.findFailedTransactions("2026-07-22"))
                                .contains("Aucun paiement en échec")
                                .contains("2026");
        }

        @Test
        void getStatistics_returnsFormattedSummary() {
                when(transactionQueryService.getStatistics())
                                .thenReturn(Map.of("failedToday", 5L, "total", 10L, "pendingTotal", 1L));

                assertThat(tools.getStatistics())
                                .contains("Synthèse")
                                .contains("Échecs aujourd")
                                .contains("5");
        }

        @Test
        void findTransactionsByProvider_acceptsDisplayName() {
                when(transactionQueryService.findByProvider(PaymentProvider.ORANGE_MONEY))
                                .thenReturn(List.of(sample("TX45910", TransactionStatus.SUCCESS, null)));

                String result = tools.findTransactionsByProvider("Orange Money");

                assertThat(result).contains("TX45910").contains("Orange Money");
        }

        @Test
        void explainFailure_includesCauseAndContext() {
                PaymentTransaction tx = sample("TX45893", TransactionStatus.FAILED, "ERROR_105");
                when(transactionQueryService.findByReference("TX45893"))
                                .thenReturn(Optional.of(tx));

                String result = tools.explainTransactionFailure("TX45893");

                assertThat(result)
                                .contains("TX45893")
                                .contains("ERROR_105")
                                .contains("solde")
                                .contains("Wave");
        }

        private static PaymentTransaction sample(
                        String reference,
                        TransactionStatus status,
                        String errorCode) {
                return new PaymentTransaction(
                                UUID.randomUUID(),
                                reference,
                                new BigDecimal("25000.00"),
                                "XOF",
                                PaymentProvider.WAVE,
                                status,
                                errorCode,
                                UUID.randomUUID(),
                                Instant.parse("2026-07-22T14:10:00Z"),
                                Instant.parse("2026-07-22T14:11:00Z"));
        }
}
