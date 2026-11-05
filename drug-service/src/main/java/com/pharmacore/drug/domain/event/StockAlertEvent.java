package com.pharmacore.drug.domain.event;

import java.time.Instant;

public record StockAlertEvent(
        String drugId,
        String drugName,
        String dci,
        int currentStock,
        int threshold,
        Instant occurredAt
) {
    public static StockAlertEvent of(String drugId, String drugName,
                                     String dci, int currentStock, int threshold) {
        return new StockAlertEvent(drugId, drugName, dci,
                currentStock, threshold, Instant.now());
    }
}
