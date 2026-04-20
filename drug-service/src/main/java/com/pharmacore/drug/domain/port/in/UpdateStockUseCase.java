package com.pharmacore.drug.domain.port.in;

import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugId;

public interface UpdateStockUseCase {

    Drug updateStock(UpdateStockCommand command);

    record UpdateStockCommand(DrugId drugId, int newStock) {}
}
