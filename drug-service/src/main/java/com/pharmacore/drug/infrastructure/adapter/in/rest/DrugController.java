package com.pharmacore.drug.infrastructure.adapter.in.rest;

import com.pharmacore.drug.domain.model.DrugId;
import com.pharmacore.drug.domain.port.in.CreateDrugUseCase;
import com.pharmacore.drug.domain.port.in.GetDrugUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final CreateDrugUseCase createDrugUseCase;
    private final GetDrugUseCase getDrugUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DrugResponse create(@Valid @RequestBody CreateDrugRequest request) {
        var command = new CreateDrugUseCase.CreateDrugCommand(
                request.name(), request.dci(),
                request.category(), request.unit(),
                request.minStockThreshold()
        );
        return DrugResponse.from(createDrugUseCase.createDrug(command));
    }

    @GetMapping("/{id}")
    public DrugResponse getById(@PathVariable String id) {
        return DrugResponse.from(getDrugUseCase.getDrugById(DrugId.of(id)));
    }

    @GetMapping
    public List<DrugResponse> getAll() {
        return getDrugUseCase.getAllDrugs().stream()
                .map(DrugResponse::from)
                .toList();
    }
}