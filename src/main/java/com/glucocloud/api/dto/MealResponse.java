package com.glucocloud.api.dto;

import com.glucocloud.api.entity.Meal;
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
public class MealResponse {

    private UUID id;
    private String description;
    private Integer carbsGrams;
    private Integer calories;
    private Integer proteinGrams;
    private Integer fatGrams;
    private Meal.MealType mealType;
    private LocalDateTime consumedAt;
    private String photoUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Analytics fields
    private boolean highCarb;
    private boolean lowCarb;
    private double carbRatio;

    public static MealResponse fromEntity(Meal meal) {
        return MealResponse.builder()
                .id(meal.getId())
                .description(meal.getDescription())
                .carbsGrams(meal.getCarbsGrams())
                .calories(meal.getCalories())
                .proteinGrams(meal.getProteinGrams())
                .fatGrams(meal.getFatGrams())
                .mealType(meal.getMealType())
                .consumedAt(meal.getConsumedAt())
                .photoUrl(meal.getPhotoUrl())
                .notes(meal.getNotes())
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .highCarb(meal.isHighCarb())
                .lowCarb(meal.isLowCarb())
                .carbRatio(Math.round(meal.getCarbRatio() * 100.0) / 100.0)
                .build();
    }
}