package com.pharmacore.drug.infrastructure.adapter.out.persistence;

import com.pharmacore.drug.domain.model.DrugCategory;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "drugs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DrugJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String dci;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrugCategory category;

    @Column(nullable = false)
    private String unit;

    @Column(name = "min_stock_threshold")
    private int minStockThreshold;
}
