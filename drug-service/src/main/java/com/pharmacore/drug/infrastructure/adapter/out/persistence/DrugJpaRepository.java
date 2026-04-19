package com.pharmacore.drug.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DrugJpaRepository extends JpaRepository<DrugJpaEntity, UUID> {
    boolean existsByDci(String dci);
}
