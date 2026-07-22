package com.aipa.infrastructure.persistence;

import com.aipa.domain.model.Customer;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.infrastructure.persistence.entity.CustomerEntity;
import com.aipa.infrastructure.persistence.entity.TransactionEntity;

public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static PaymentTransaction toDomain(TransactionEntity entity) {
        return new PaymentTransaction(
                entity.getId(),
                entity.getReference(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getProvider(),
                entity.getStatus(),
                entity.getErrorCode(),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPhone(),
                entity.getEmail());
    }
}
