package com.pharmacore.drug.infrastructure.adapter.in.rest;

import com.pharmacore.drug.domain.model.DrugCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateDrugRequest(
        @NotBlank String name,
        @NotBlank String dci,
        @NotNull  DrugCategory category,
        @NotBlank String unit,
        @Positive int minStockThreshold
) {}