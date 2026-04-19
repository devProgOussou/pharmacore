package com.pharmacore.drug.domain.port.in;

import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugId;

import java.util.List;

public interface GetDrugUseCase {
    Drug getDrugById(DrugId id);
    List<Drug> getAllDrugs();
}