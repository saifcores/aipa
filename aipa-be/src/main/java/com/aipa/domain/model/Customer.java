package com.aipa.domain.model;

import java.util.UUID;

public record Customer(
        UUID id,
        String firstname,
        String lastname,
        String phone,
        String email) {
    public String fullName() {
        return firstname + " " + lastname;
    }
}
