package com.pharmacore.drug.application.service;

import com.pharmacore.drug.domain.event.StockAlertEvent;
import com.pharmacore.drug.domain.exception.DrugNotFoundException;
import com.pharmacore.drug.domain.exception.DuplicateDciException;
import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugId;
import com.pharmacore.drug.domain.port.in.CreateDrugUseCase;
import com.pharmacore.drug.domain.port.in.GetDrugUseCase;
import com.pharmacore.drug.domain.port.in.UpdateStockUseCase;
import com.pharmacore.drug.domain.port.out.DrugEventPublisherPort;
import com.pharmacore.drug.domain.port.out.DrugRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DrugApplicationService
        implements CreateDrugUseCase, GetDrugUseCase, UpdateStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(DrugApplicationService.class);

    private final DrugRepositoryPort drugRepositoryPort;
    private final DrugEventPublisherPort drugEventPublisherPort;

    public DrugApplicationService(DrugRepositoryPort drugRepositoryPort,
                                  DrugEventPublisherPort drugEventPublisherPort) {
        this.drugRepositoryPort = drugRepositoryPort;
        this.drugEventPublisherPort = drugEventPublisherPort;
    }

    @Override
    public Drug createDrug(CreateDrugCommand command) {
        if (drugRepositoryPort.existsByDci(command.dci())) {
            throw new DuplicateDciException(command.dci());
        }
        Drug drug = Drug.create(command.name(), command.dci(),
                command.category(), command.unit(), command.minStockThreshold());
        return drugRepositoryPort.save(drug);
    }

    @Override
    public Drug getDrugById(DrugId id) {
        return drugRepositoryPort.findById(id)
                .orElseThrow(() -> new DrugNotFoundException(id));
    }

    @Override
    public List<Drug> getAllDrugs() {
        return drugRepositoryPort.findAll();
    }

    @Override
    public Drug updateStock(UpdateStockCommand command) {
        Drug drug = getDrugById(command.drugId());

        log.info("[STOCK] Updating stock for drug={} newStock={}",
                drug.name(), command.newStock());

        if (drug.isLowStock(command.newStock())) {
            log.warn("[STOCK] Low stock detected for drug={} stock={} threshold={}",
                    drug.name(), command.newStock(), drug.minStockThreshold());

            drugEventPublisherPort.publishStockAlert(
                    StockAlertEvent.of(
                            drug.id().value().toString(),
                            drug.name(),
                            drug.dci(),
                            command.newStock(),
                            drug.minStockThreshold()
                    )
            );
        }

        return drug;
    }
}
