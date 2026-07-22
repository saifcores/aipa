package com.aipa.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentTransaction(
                UUID id,
                String reference,
                BigDecimal amount,
                String currency,
                PaymentProvider provider,
                TransactionStatus status,
                String errorCode,
                UUID customerId,
                Instant createdAt,
                Instant updatedAt) {
}
