package com.glucocloud.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationSummaryResponse {

    private int totalMedications;
    private int insulinDoses;
    private int oralMedications;
    private int injectableMedications;

    // Effectiveness
    private Double averageEffectivenessRating;
    private int medicationsWithSideEffects;
    private double sideEffectsPercentage;

    // Medication adherence (estimated based on frequency)
    private String adherenceEstimate; // "EXCELLENT", "GOOD", "POOR", "INSUFFICIENT_DATA"
    private double medicationsPerDay;

    // Medication types
    private List<String> uniqueMedicationNames;
    private int uniqueMedicationCount;

    // Time period
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    // Patterns
    private String mostCommonMedicationType;
    private int missedDosesEstimate;
}