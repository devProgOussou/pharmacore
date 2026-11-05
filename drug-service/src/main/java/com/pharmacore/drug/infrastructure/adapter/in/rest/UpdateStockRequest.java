package com.pharmacore.drug.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.PositiveOrZero;

public record UpdateStockRequest(@PositiveOrZero int newStock) {}