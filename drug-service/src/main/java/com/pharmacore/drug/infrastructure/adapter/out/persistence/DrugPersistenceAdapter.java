package com.pharmacore.drug.infrastructure.adapter.out.persistence;

import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugId;
import com.pharmacore.drug.domain.port.out.DrugRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DrugPersistenceAdapter implements DrugRepositoryPort {

    private final DrugJpaRepository jpaRepository;

    @Override
    public Drug save(Drug drug) {
        return toDomain(jpaRepository.save(toEntity(drug)));
    }

    @Override
    public Optional<Drug> findById(DrugId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Drug> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByDci(String dci) {
        return jpaRepository.existsByDci(dci);
    }

    private DrugJpaEntity toEntity(Drug drug) {
        return DrugJpaEntity.builder()
                .id(drug.id().value())
                .name(drug.name())
                .dci(drug.dci())
                .category(drug.category())
                .unit(drug.unit())
                .minStockThreshold(drug.minStockThreshold())
                .build();
    }

    private Drug toDomain(DrugJpaEntity e) {
        return new Drug(
                new DrugId(e.getId()),
                e.getName(), e.getDci(),
                e.getCategory(), e.getUnit(),
                e.getMinStockThreshold()
        );
    }
}
