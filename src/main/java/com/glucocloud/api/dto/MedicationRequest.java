package com.glucocloud.api.dto;

import com.glucocloud.api.entity.Medication;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicationRequest {

    @NotBlank(message = "Medication name is required")
    @Size(max = 200, message = "Medication name cannot exceed 200 characters")
    private String name;

    @Size(max = 100, message = "Dosage cannot exceed 100 characters")
    private String dosage;

    private Medication.MedicationType medicationType;

    @NotNull(message = "Medication timestamp is required")
    private LocalDateTime takenAt;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Min(value = 1, message = "Effectiveness rating must be between 1 and 5")
    @Max(value = 5, message = "Effectiveness rating must be between 1 and 5")
    private Integer effectivenessRating;

    @Size(max = 500, message = "Side effects cannot exceed 500 characters")
    private String sideEffects;
}