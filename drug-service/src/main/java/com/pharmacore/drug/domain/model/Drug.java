package com.pharmacore.drug.domain.model;

public record Drug(
        DrugId id,
        String name,
        String dci,
        DrugCategory category,
        String unit,
        int minStockThreshold
) {
    public static Drug create(String name, String dci, DrugCategory category, String unit, int minStockThreshold) {
        return new Drug(DrugId.generate(), name, dci, category, unit, minStockThreshold);
    }

    public boolean isLowStock(int currentStock) {
        return currentStock <= minStockThreshold;
    }
}
