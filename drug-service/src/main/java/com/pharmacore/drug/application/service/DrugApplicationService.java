package com.pharmacore.drug.application.service;

import com.pharmacore.drug.domain.exception.DrugNotFoundException;
import com.pharmacore.drug.domain.exception.DuplicateDciException;
import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugId;
import com.pharmacore.drug.domain.port.in.CreateDrugUseCase;
import com.pharmacore.drug.domain.port.in.GetDrugUseCase;
import com.pharmacore.drug.domain.port.out.DrugRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DrugApplicationService implements CreateDrugUseCase, GetDrugUseCase {

    private final DrugRepositoryPort drugRepositoryPort;

    @Override
    public Drug createDrug(CreateDrugCommand command) {
        if (drugRepositoryPort.existsByDci(command.dci())) {
            throw new DuplicateDciException(command.dci());
        }
        Drug drug = Drug.create(
                command.name(), command.dci(),
                command.category(), command.unit(),
                command.minStockThreshold()
        );
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
}