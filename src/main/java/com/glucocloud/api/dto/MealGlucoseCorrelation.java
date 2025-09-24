package com.glucocloud.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealGlucoseCorrelation {

    private UUID mealId;
    private String mealDescription;
    private Integer carbsGrams;
    private LocalDateTime mealTime;

    private UUID glucoseReadingId;
    private BigDecimal preGlucoseValue;
    private BigDecimal postGlucoseValue;
    private BigDecimal glucoseRise;
    private LocalDateTime preGlucoseTime;
    private LocalDateTime postGlucoseTime;

    private String impact; // "HIGH_IMPACT", "MODERATE_IMPACT", "LOW_IMPACT", "NO_DATA"
    private double carbToGlucoseRatio; // mg/dL per gram of carbs
    private int minutesToPeak;
}