package com.pharmacore.drug.domain.port.in;

import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugCategory;

public interface CreateDrugUseCase {
    Drug createDrug(CreateDrugCommand command);

    record CreateDrugCommand(
      String name,
      String dci,
      DrugCategory category,
      String unit,
      int minStockThreshold
    ) {}
}