package com.pharmacore.drug.application.service;

import com.pharmacore.drug.domain.exception.DrugNotFoundException;
import com.pharmacore.drug.domain.exception.DuplicateDciException;
import com.pharmacore.drug.domain.model.Drug;
import com.pharmacore.drug.domain.model.DrugCategory;
import com.pharmacore.drug.domain.model.DrugId;
import com.pharmacore.drug.domain.port.in.CreateDrugUseCase.CreateDrugCommand;
import com.pharmacore.drug.domain.port.out.DrugRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrugApplicationServiceTest {

    @Mock
    private DrugRepositoryPort drugRepositoryPort;

    @InjectMocks
    private DrugApplicationService drugApplicationService;

    private Drug paracetamol;
    private DrugId paracetamolId;

    @BeforeEach
    void setUp() {
        paracetamolId = new DrugId(UUID.randomUUID());
        paracetamol = new Drug(
                paracetamolId,
                "Paracétamol",
                "paracetamol",
                DrugCategory.ANALGESIQUE,
                "comprimé",
                100
        );
    }

    @Nested
    @DisplayName("createDrug()")
    class CreateDrug {

        private CreateDrugCommand validCommand;

        @BeforeEach
        void setUp() {
            validCommand = new CreateDrugCommand(
                    "Paracétamol",
                    "paracetamol",
                    DrugCategory.ANALGESIQUE,
                    "comprimé",
                    100
            );
        }

        @Test
        @DisplayName("doit créer et retourner le médicament quand la DCI est unique")
        void shouldCreateDrug_whenDciIsUnique() {
            when(drugRepositoryPort.existsByDci("paracetamol")).thenReturn(false);
            when(drugRepositoryPort.save(any(Drug.class))).thenReturn(paracetamol);

            Drug result = drugApplicationService.createDrug(validCommand);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Paracétamol");
            assertThat(result.dci()).isEqualTo("paracetamol");
            assertThat(result.category()).isEqualTo(DrugCategory.ANALGESIQUE);

            verify(drugRepositoryPort, times(1)).existsByDci("paracetamol");
            verify(drugRepositoryPort, times(1)).save(any(Drug.class));
        }

        @Test
        @DisplayName("doit lever DuplicateDciException quand la DCI existe déjà")
        void shouldThrowDuplicateDciException_whenDciAlreadyExists() {
            when(drugRepositoryPort.existsByDci("paracetamol")).thenReturn(true);

            assertThatThrownBy(() -> drugApplicationService.createDrug(validCommand))
                    .isInstanceOf(DuplicateDciException.class)
                    .hasMessageContaining("paracetamol");

            verify(drugRepositoryPort, never()).save(any(Drug.class));
        }

        @Test
        @DisplayName("ne doit jamais appeler save() si la DCI est dupliquée")
        void shouldNeverCallSave_whenDciIsDuplicate() {
            when(drugRepositoryPort.existsByDci(any())).thenReturn(true);

            assertThatThrownBy(() -> drugApplicationService.createDrug(validCommand))
                    .isInstanceOf(DuplicateDciException.class);

            verifyNoMoreInteractions(drugRepositoryPort);
        }
    }

    @Nested
    @DisplayName("getDrugById()")
    class GetDrugById {

        @Test
        @DisplayName("doit retourner le médicament quand l'ID existe")
        void shouldReturnDrug_whenIdExists() {
            when(drugRepositoryPort.findById(paracetamolId))
                    .thenReturn(Optional.of(paracetamol));

            Drug result = drugApplicationService.getDrugById(paracetamolId);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(paracetamolId);
            assertThat(result.name()).isEqualTo("Paracétamol");
            verify(drugRepositoryPort, times(1)).findById(paracetamolId);
        }

        @Test
        @DisplayName("doit lever DrugNotFoundException quand l'ID est inconnu")
        void shouldThrowDrugNotFoundException_whenIdNotFound() {
            DrugId unknownId = new DrugId(UUID.randomUUID());
            when(drugRepositoryPort.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> drugApplicationService.getDrugById(unknownId))
                    .isInstanceOf(DrugNotFoundException.class)
                    .hasMessageContaining(unknownId.value().toString());

            verify(drugRepositoryPort, times(1)).findById(unknownId);
        }
    }

    @Nested
    @DisplayName("getAllDrugs()")
    class GetAllDrugs {

        @Test
        @DisplayName("doit retourner la liste complète des médicaments")
        void shouldReturnAllDrugs() {
            Drug amoxicilline = new Drug(
                    new DrugId(UUID.randomUUID()),
                    "Amoxicilline",
                    "amoxicillin",
                    DrugCategory.ANTIBIOTIQUE,
                    "gélule",
                    50
            );
            when(drugRepositoryPort.findAll()).thenReturn(List.of(paracetamol, amoxicilline));

            List<Drug> result = drugApplicationService.getAllDrugs();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Drug::dci)
                    .containsExactlyInAnyOrder("paracetamol", "amoxicillin");
            verify(drugRepositoryPort, times(1)).findAll();
        }

        @Test
        @DisplayName("doit retourner une liste vide quand il n'y a aucun médicament")
        void shouldReturnEmptyList_whenNoDrugsExist() {
            when(drugRepositoryPort.findAll()).thenReturn(List.of());

            List<Drug> result = drugApplicationService.getAllDrugs();

            assertThat(result).isEmpty();
            verify(drugRepositoryPort, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("isLowStock()")
    class IsLowStock {

        @Test
        @DisplayName("doit détecter un stock faible quand le stock est égal au seuil")
        void shouldDetectLowStock_whenStockEqualsThreshold() {
            assertThat(paracetamol.isLowStock(100)).isTrue();
        }

        @Test
        @DisplayName("doit détecter un stock faible quand le stock est en dessous du seuil")
        void shouldDetectLowStock_whenStockBelowThreshold() {
            assertThat(paracetamol.isLowStock(50)).isTrue();
        }

        @Test
        @DisplayName("ne doit pas détecter un stock faible quand le stock est suffisant")
        void shouldNotDetectLowStock_whenStockAboveThreshold() {
            assertThat(paracetamol.isLowStock(200)).isFalse();
        }
    }
}
