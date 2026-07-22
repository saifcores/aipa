package com.aipa.ai.replies;

import static org.assertj.core.api.Assertions.assertThat;

import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OpsReplyFormatterTest {

        private final OpsReplyFormatter formatter = new OpsReplyFormatter();

        @Test
        void singleTransaction_isOperatorFriendly() {
                String text = formatter.singleTransaction(tx(
                                "TX45892",
                                TransactionStatus.SUCCESS,
                                null,
                                new BigDecimal("25000.00")));

                assertThat(text)
                                .contains("TX45892")
                                .contains("confirmée")
                                .contains("25")
                                .contains("FCFA")
                                .contains("Wave")
                                .doesNotContain("status=");
        }

        @Test
        void failureExplanation_mapsErrorCatalog() {
                String text = formatter.failureExplanation(tx(
                                "TX45893",
                                TransactionStatus.FAILED,
                                "ERROR_105",
                                new BigDecimal("120000.00")));

                assertThat(text)
                                .contains("TX45893")
                                .contains("ERROR_105")
                                .contains("Insuffisance de solde")
                                .contains("120");
        }

        @Test
        void failedList_numbersEntries() {
                String text = formatter.failedList(
                                java.time.LocalDate.of(2026, 7, 22),
                                List.of(tx(
                                                "TX45901",
                                                TransactionStatus.FAILED,
                                                "ERROR_105",
                                                new BigDecimal("8000"))));

                assertThat(text)
                                .contains("1 paiement")
                                .contains("1. TX45901")
                                .contains("ERROR_105");
        }

        private static PaymentTransaction tx(
                        String reference,
                        TransactionStatus status,
                        String errorCode,
                        BigDecimal amount) {
                return new PaymentTransaction(
                                UUID.randomUUID(),
                                reference,
                                amount,
                                "XOF",
                                PaymentProvider.WAVE,
                                status,
                                errorCode,
                                UUID.randomUUID(),
                                Instant.parse("2026-07-22T14:10:00Z"),
                                Instant.parse("2026-07-22T14:11:00Z"));
        }
}
