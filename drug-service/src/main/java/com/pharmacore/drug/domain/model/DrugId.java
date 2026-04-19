package com.pharmacore.drug.domain.model;

import java.util.UUID;

public record DrugId(UUID value) {

    public static DrugId generate() {
        return new DrugId(UUID.randomUUID());
    }

    public static DrugId of(String value) {
        return new DrugId(UUID.fromString(value));
    }
}
