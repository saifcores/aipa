package com.aipa.infrastructure.persistence.repository;

import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.TransactionStatus;
import com.aipa.infrastructure.persistence.entity.TransactionEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

        Optional<TransactionEntity> findByReferenceIgnoreCaseAndDeletedFalse(String reference);

        List<TransactionEntity> findByDeletedFalseOrderByCreatedAtDesc();

        List<TransactionEntity> findByStatusAndCreatedAtGreaterThanEqualAndDeletedFalse(
                        TransactionStatus status,
                        Instant createdAt);

        long countByStatusAndDeletedFalse(TransactionStatus status);

        long countByStatusAndCreatedAtGreaterThanEqualAndDeletedFalse(
                        TransactionStatus status,
                        Instant createdAt);

        List<TransactionEntity> findByProviderAndDeletedFalseOrderByCreatedAtDesc(
                        PaymentProvider provider);

        List<TransactionEntity> findByProviderAndAmountGreaterThanEqualAndDeletedFalseOrderByCreatedAtDesc(
                        PaymentProvider provider,
                        BigDecimal minAmount);

        @Query("""
                        select t.customerId, count(t)
                        from TransactionEntity t
                        where t.status = com.aipa.domain.model.TransactionStatus.FAILED
                          and t.deleted = false
                          and t.createdAt >= :since
                        group by t.customerId
                        having count(t) >= :minFailures
                        order by count(t) desc
                        """)
        List<Object[]> findCustomersWithMultipleFailures(
                        @Param("since") Instant since,
                        @Param("minFailures") long minFailures);

        @Query("""
                        select t.status, count(t)
                        from TransactionEntity t
                        where t.deleted = false
                        group by t.status
                        """)
        List<Object[]> countGroupedByStatus();

        @Query("""
                        select t.provider, count(t)
                        from TransactionEntity t
                        where t.deleted = false
                        group by t.provider
                        """)
        List<Object[]> countGroupedByProvider();
}
