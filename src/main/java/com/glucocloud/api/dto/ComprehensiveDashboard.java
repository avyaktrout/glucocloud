package com.glucocloud.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensiveDashboard {

    private LocalDateTime generatedAt;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    // Glucose Summary
    private GlucoseSummaryResponse glucoseSummary;

    // Meal Summary
    private MealSummaryResponse mealSummary;

    // Medication Summary
    private MedicationSummaryResponse medicationSummary;

    // Correlations and Insights
    private List<MealGlucoseCorrelation> mealGlucoseCorrelations;
    private Map<String, String> insights;
    private Map<String, String> recommendations;

    // Health Score (0-100)
    private int healthScore;
    private String healthScoreDescription;

    // Progress Tracking
    private Map<String, Object> progressMetrics;
}