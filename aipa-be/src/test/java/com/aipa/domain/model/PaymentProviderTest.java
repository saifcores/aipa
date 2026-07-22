package com.aipa.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class PaymentProviderTest {

        @Test
        void fromDisplayName_parsesCommonAliases() {
                assertThat(PaymentProvider.fromDisplayName("Wave"))
                                .isEqualTo(PaymentProvider.WAVE);
                assertThat(PaymentProvider.fromDisplayName("orange money"))
                                .isEqualTo(PaymentProvider.ORANGE_MONEY);
                assertThat(PaymentProvider.fromDisplayName("Free-Money"))
                                .isEqualTo(PaymentProvider.FREE_MONEY);
        }

        @Test
        void fromDisplayName_rejectsBlank() {
                assertThatThrownBy(() -> PaymentProvider.fromDisplayName(" "))
                                .isInstanceOf(IllegalArgumentException.class);
        }
}
