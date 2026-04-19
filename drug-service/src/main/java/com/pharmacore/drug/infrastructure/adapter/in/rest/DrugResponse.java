package com.pharmacore.drug.infrastructure.adapter.in.rest;

import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugCategory;

public record DrugResponse(
        String id,
        String name,
        String dci,
        DrugCategory category,
        String unit,
        int minStockThreshold
) {
    public static DrugResponse from(Drug drug) {
        return new DrugResponse(
                drug.id().value().toString(),
                drug.name(), drug.dci(),
                drug.category(), drug.unit(),
                drug.minStockThreshold()
        );
    }
}