package com.pharmacore.drug.domain.exception;

public class DuplicateDciException extends RuntimeException {
    public DuplicateDciException(String dci) {
        super("Un médicament avec la DCI '" + dci + "' existe déjà");
    }
}