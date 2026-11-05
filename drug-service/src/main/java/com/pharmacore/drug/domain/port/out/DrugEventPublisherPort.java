package com.pharmacore.drug.domain.port.out;

import com.pharmacore.drug.domain.event.StockAlertEvent;

public interface DrugEventPublisherPort {
    void publishStockAlert(StockAlertEvent event);
}
