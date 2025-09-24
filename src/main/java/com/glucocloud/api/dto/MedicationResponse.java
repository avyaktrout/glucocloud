package com.glucocloud.api.dto;

import com.glucocloud.api.entity.Medication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationResponse {

    private UUID id;
    private String name;
    private String dosage;
    private Medication.MedicationType medicationType;
    private LocalDateTime takenAt;
    private String notes;
    private Integer effectivenessRating;
    private String sideEffects;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Analytics fields
    private boolean isInsulin;
    private boolean isOralMedication;
    private boolean isInjectable;

    public static MedicationResponse fromEntity(Medication medication) {
        return MedicationResponse.builder()
                .id(medication.getId())
                .name(medication.getName())
                .dosage(medication.getDosage())
                .medicationType(medication.getMedicationType())
                .takenAt(medication.getTakenAt())
                .notes(medication.getNotes())
                .effectivenessRating(medication.getEffectivenessRating())
                .sideEffects(medication.getSideEffects())
                .createdAt(medication.getCreatedAt())
                .updatedAt(medication.getUpdatedAt())
                .isInsulin(medication.isInsulin())
                .isOralMedication(medication.isOralMedication())
                .isInjectable(medication.isInjectable())
                .build();
    }
}