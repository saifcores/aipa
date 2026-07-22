package com.aipa.api.dto;

import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String reference,
        BigDecimal amount,
        String currency,
        PaymentProvider provider,
        String providerLabel,
        TransactionStatus status,
        String errorCode,
        UUID customerId,
        Instant createdAt,
        Instant updatedAt) {
    public static TransactionResponse from(PaymentTransaction tx) {
        return new TransactionResponse(
                tx.id(),
                tx.reference(),
                tx.amount(),
                tx.currency(),
                tx.provider(),
                tx.provider().displayName(),
                tx.status(),
                tx.errorCode(),
                tx.customerId(),
                tx.createdAt(),
                tx.updatedAt());
    }
}
