package com.pharmacore.drug.domain.exception;

import com.pharmacore.drug.domain.model.DrugId;

public class DrugNotFoundException extends RuntimeException {
    public DrugNotFoundException(DrugId id) {
        super("Médicament introuvable : " + id.value());
    }
}