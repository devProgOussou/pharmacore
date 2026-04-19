package com.pharmacore.drug.domain.port.out;

import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugId;

import java.util.List;
import java.util.Optional;

public interface DrugRepositoryPort {
    Drug save(Drug drug);
    Optional<Drug> findById(DrugId id);
    List<Drug> findAll();
    boolean existsByDci(String dci);
}