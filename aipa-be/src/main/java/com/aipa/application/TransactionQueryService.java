package com.aipa.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aipa.domain.catalog.ErrorCodeCatalog;
import com.aipa.domain.model.Customer;
import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import com.aipa.infrastructure.persistence.PersistenceMapper;
import com.aipa.infrastructure.persistence.repository.CustomerJpaRepository;
import com.aipa.infrastructure.persistence.repository.TransactionJpaRepository;

@Service
@Transactional(readOnly = true)
public class TransactionQueryService {

    private final TransactionJpaRepository transactionRepository;
    private final CustomerJpaRepository customerRepository;
    private final Clock clock;

    public TransactionQueryService(
            TransactionJpaRepository transactionRepository,
            CustomerJpaRepository customerRepository,
            Clock clock) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.clock = clock;
    }

    public List<PaymentTransaction> findAll() {
        return transactionRepository.findByDeletedFalseOrderByCreatedAtDesc().stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }

    public Optional<PaymentTransaction> findById(UUID id) {
        return transactionRepository.findById(id)
                .filter(entity -> !entity.isDeleted())
                .map(PersistenceMapper::toDomain);
    }

    public Optional<PaymentTransaction> findByReference(String reference) {
        return transactionRepository
                .findByReferenceIgnoreCaseAndDeletedFalse(reference)
                .map(PersistenceMapper::toDomain);
    }

    public List<PaymentTransaction> findFailedOn(LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return transactionRepository
                .findByStatusAndCreatedAtGreaterThanEqualAndDeletedFalse(
                        TransactionStatus.FAILED, start)
                .stream()
                .map(PersistenceMapper::toDomain)
                .filter(tx -> tx.createdAt().isBefore(end))
                .toList();
    }

    public long countByStatus(TransactionStatus status) {
        return transactionRepository.countByStatusAndDeletedFalse(status);
    }

    public long countByStatusSince(TransactionStatus status, Instant since) {
        return transactionRepository
                .countByStatusAndCreatedAtGreaterThanEqualAndDeletedFalse(status, since);
    }

    public List<PaymentTransaction> findByProvider(PaymentProvider provider) {
        return transactionRepository
                .findByProviderAndDeletedFalseOrderByCreatedAtDesc(provider)
                .stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }

    public List<PaymentTransaction> findByProviderAboveAmount(
            PaymentProvider provider,
            BigDecimal minAmount) {
        return transactionRepository
                .findByProviderAndAmountGreaterThanEqualAndDeletedFalseOrderByCreatedAtDesc(
                        provider, minAmount)
                .stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }

    public String explainFailure(String reference) {
        return findByReference(reference)
                .map(tx -> {
                    if (tx.status() != TransactionStatus.FAILED) {
                        return "La transaction " + reference + " n'est pas en échec "
                                + "(statut actuel : " + tx.status() + ").";
                    }
                    if (tx.errorCode() == null || tx.errorCode().isBlank()) {
                        return "La transaction " + reference
                                + " a échoué sans code d'erreur renseigné.";
                    }
                    return ErrorCodeCatalog.explainOrUnknown(tx.errorCode());
                })
                .orElse("Aucune transaction trouvée pour la référence " + reference + ".");
    }

    public Map<String, Object> getStatistics() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (TransactionStatus status : TransactionStatus.values()) {
            byStatus.put(status.name(), 0L);
        }
        transactionRepository.countGroupedByStatus().forEach(row -> {
            TransactionStatus status = (TransactionStatus) row[0];
            byStatus.put(status.name(), (Long) row[1]);
        });

        Map<String, Long> byProvider = new LinkedHashMap<>();
        transactionRepository.countGroupedByProvider().forEach(row -> {
            PaymentProvider provider = (PaymentProvider) row[0];
            byProvider.put(provider.displayName(), (Long) row[1]);
        });

        Instant startOfDay = LocalDate.now(clock)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
        long failedToday = countByStatusSince(TransactionStatus.FAILED, startOfDay);
        long pendingTotal = countByStatus(TransactionStatus.PENDING);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", transactionRepository.count());
        stats.put("byStatus", byStatus);
        stats.put("byProvider", byProvider);
        stats.put("failedToday", failedToday);
        stats.put("pendingTotal", pendingTotal);
        return stats;
    }

    public List<Map<String, Object>> findCustomersWithMultipleFailuresThisWeek(
            long minFailures) {
        Instant weekStart = LocalDate.now(clock)
                .minusDays(7)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
        return transactionRepository
                .findCustomersWithMultipleFailures(weekStart, minFailures)
                .stream()
                .map(row -> {
                    UUID customerId = (UUID) row[0];
                    Long failures = (Long) row[1];
                    Optional<Customer> customer = customerRepository.findById(customerId)
                            .filter(entity -> !entity.isDeleted())
                            .map(PersistenceMapper::toDomain);
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("customerId", customerId);
                    entry.put("failureCount", failures);
                    customer.ifPresent(c -> {
                        entry.put("fullname", c.fullName());
                        entry.put("phone", c.phone());
                        entry.put("email", c.email());
                    });
                    return entry;
                })
                .toList();
    }

    public Instant startOfToday() {
        return LocalDate.now(clock).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }
}
