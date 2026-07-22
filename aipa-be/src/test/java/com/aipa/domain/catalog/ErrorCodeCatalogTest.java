package com.aipa.domain.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ErrorCodeCatalogTest {

    @Test
    void describe_knownCode() {
        assertThat(ErrorCodeCatalog.describe("ERROR_105"))
                .contains("Insuffisance de solde");
    }

    @Test
    void explainOrUnknown_unknownCode() {
        assertThat(ErrorCodeCatalog.explainOrUnknown("ERROR_999"))
                .contains("Aucun descriptif");
    }
}
