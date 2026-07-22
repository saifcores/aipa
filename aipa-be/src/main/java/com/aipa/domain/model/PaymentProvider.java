package com.aipa.domain.model;

public enum PaymentProvider {
    WAVE,
    ORANGE_MONEY,
    FREE_MONEY,
    VISA,
    MASTERCARD;

    public String displayName() {
        return switch (this) {
            case WAVE -> "Wave";
            case ORANGE_MONEY -> "Orange Money";
            case FREE_MONEY -> "Free Money";
            case VISA -> "Visa";
            case MASTERCARD -> "Mastercard";
        };
    }

    public static PaymentProvider fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Provider is required");
        }
        String normalized = value.trim().toLowerCase()
                .replace('-', ' ')
                .replace('_', ' ');
        return switch (normalized) {
            case "wave" -> WAVE;
            case "orange money", "orangemoney", "orange" -> ORANGE_MONEY;
            case "free money", "freemoney", "free" -> FREE_MONEY;
            case "visa" -> VISA;
            case "mastercard", "master card" -> MASTERCARD;
            default -> PaymentProvider.valueOf(
                    value.trim().toUpperCase().replace(' ', '_'));
        };
    }
}
