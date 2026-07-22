package com.aipa.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import com.aipa.infrastructure.persistence.entity.TransactionEntity;
import com.aipa.infrastructure.persistence.repository.CustomerJpaRepository;
import com.aipa.infrastructure.persistence.repository.TransactionJpaRepository;

@ExtendWith(MockitoExtension.class)
class TransactionQueryServiceTest {

        @Mock
        private TransactionJpaRepository transactionRepository;

        @Mock
        private CustomerJpaRepository customerRepository;

        private TransactionQueryService service;

        private final Clock clock = Clock.fixed(
                        Instant.parse("2026-07-22T12:00:00Z"), ZoneOffset.UTC);

        @BeforeEach
        void setUp() {
                service = new TransactionQueryService(
                                transactionRepository, customerRepository, clock);
        }

        @Test
        void findByReference_returnsMappedDomainObject() {
                TransactionEntity entity = entity(
                                "TX45892",
                                new BigDecimal("25000.00"),
                                PaymentProvider.WAVE,
                                TransactionStatus.SUCCESS,
                                null);

                when(transactionRepository.findByReferenceIgnoreCaseAndDeletedFalse("TX45892"))
                                .thenReturn(Optional.of(entity));

                Optional<PaymentTransaction> result = service.findByReference("TX45892");

                assertThat(result).isPresent();
                assertThat(result.get().reference()).isEqualTo("TX45892");
                assertThat(result.get().amount()).isEqualByComparingTo("25000.00");
                assertThat(result.get().provider()).isEqualTo(PaymentProvider.WAVE);
                assertThat(result.get().status()).isEqualTo(TransactionStatus.SUCCESS);
        }

        @Test
        void explainFailure_mapsKnownErrorCode() {
                TransactionEntity entity = entity(
                                "TX45893",
                                new BigDecimal("120000.00"),
                                PaymentProvider.WAVE,
                                TransactionStatus.FAILED,
                                "ERROR_105");
                when(transactionRepository.findByReferenceIgnoreCaseAndDeletedFalse("TX45893"))
                                .thenReturn(Optional.of(entity));

                String explanation = service.explainFailure("TX45893");

                assertThat(explanation).contains("ERROR_105").contains("solde");
        }

        @Test
        void today_usesInjectedClock() {
                assertThat(service.today()).isEqualTo(LocalDate.of(2026, 7, 22));
        }

        @Test
        void countByStatus_delegatesToRepository() {
                when(transactionRepository.countByStatusAndDeletedFalse(TransactionStatus.FAILED))
                                .thenReturn(7L);

                assertThat(service.countByStatus(TransactionStatus.FAILED)).isEqualTo(7L);
        }

        @Test
        void findByProviderAboveAmount_filtersViaRepository() {
                TransactionEntity entity = entity(
                                "TX45910",
                                new BigDecimal("65000.00"),
                                PaymentProvider.ORANGE_MONEY,
                                TransactionStatus.SUCCESS,
                                null);
                when(transactionRepository
                                .findByProviderAndAmountGreaterThanEqualAndDeletedFalseOrderByCreatedAtDesc(
                                                eq(PaymentProvider.ORANGE_MONEY),
                                                eq(new BigDecimal("50000"))))
                                .thenReturn(List.of(entity));

                List<PaymentTransaction> result = service.findByProviderAboveAmount(
                                PaymentProvider.ORANGE_MONEY, new BigDecimal("50000"));

                assertThat(result).hasSize(1);
                assertThat(result.getFirst().reference()).isEqualTo("TX45910");
        }

        private static TransactionEntity entity(
                        String reference,
                        BigDecimal amount,
                        PaymentProvider provider,
                        TransactionStatus status,
                        String errorCode) {
                TransactionEntity entity = new TransactionEntity();
                ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
                ReflectionTestUtils.setField(entity, "reference", reference);
                ReflectionTestUtils.setField(entity, "amount", amount);
                ReflectionTestUtils.setField(entity, "currency", "XOF");
                ReflectionTestUtils.setField(entity, "provider", provider);
                ReflectionTestUtils.setField(entity, "status", status);
                ReflectionTestUtils.setField(entity, "errorCode", errorCode);
                ReflectionTestUtils.setField(entity, "customerId", UUID.randomUUID());
                ReflectionTestUtils.setField(entity, "createdAt", Instant.parse("2026-07-22T14:10:00Z"));
                ReflectionTestUtils.setField(entity, "updatedAt", Instant.parse("2026-07-22T14:11:00Z"));
                ReflectionTestUtils.setField(entity, "deleted", false);
                return entity;
        }
}
