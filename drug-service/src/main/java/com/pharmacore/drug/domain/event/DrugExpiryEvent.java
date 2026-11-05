package com.pharmacore.drug.domain.event;

import java.time.Instant;
import java.time.LocalDate;

public record DrugExpiryEvent(
        String batchId,
        String drugId,
        String drugName,
        LocalDate expiryDate,
        int quantity,
        Instant occurredAt
) {
    public static DrugExpiryEvent of(String batchId, String drugId,
                                     String drugName, LocalDate expiryDate, int quantity) {
        return new DrugExpiryEvent(batchId, drugId, drugName,
                expiryDate, quantity, Instant.now());
    }
}
