package com.glucocloud.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSummaryResponse {

    private int totalMeals;
    private Integer averageCarbsPerMeal;
    private Integer averageCaloriesPerMeal;
    private Integer totalCarbs;
    private Integer totalCalories;

    // Meal patterns
    private int highCarbMeals;
    private int lowCarbMeals;
    private double highCarbPercentage;
    private double lowCarbPercentage;

    // Meal timing
    private int breakfastCount;
    private int lunchCount;
    private int dinnerCount;
    private int snackCount;

    // Time period
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    // Nutrition balance
    private String nutritionBalance; // "BALANCED", "HIGH_CARB", "LOW_CARB", "INSUFFICIENT_DATA"
    private double avgCarbRatio;
}