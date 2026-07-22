package com.aipa.domain.catalog;

import java.util.Map;
import java.util.Optional;

/**
 * Business catalog of PSP / bank error codes used by support operators.
 */
public final class ErrorCodeCatalog {

    private static final Map<String, String> DESCRIPTIONS = Map.ofEntries(
            Map.entry("ERROR_105", "Insuffisance de solde"),
            Map.entry("ERROR_201", "Compte client introuvable ou inactif"),
            Map.entry("ERROR_302", "Limite journalière dépassée"),
            Map.entry("ERROR_401", "Authentification OTP échouée"),
            Map.entry("ERROR_503", "Timeout côté fournisseur de paiement"),
            Map.entry("ERROR_601", "Transaction refusée par l'émetteur"),
            Map.entry("ERROR_702", "Montant invalide ou hors plage autorisée"),
            Map.entry("ERROR_803", "Réseau du PSP indisponible"));

    private ErrorCodeCatalog() {
    }

    public static Optional<String> describe(String errorCode) {
        if (errorCode == null || errorCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(DESCRIPTIONS.get(errorCode.trim().toUpperCase()));
    }

    public static String explainOrUnknown(String errorCode) {
        return describe(errorCode)
                .map(desc -> "Le code " + errorCode + " correspond à : " + desc + ".")
                .orElse("Aucun descriptif métier connu pour le code "
                        + errorCode + ".");
    }
}
