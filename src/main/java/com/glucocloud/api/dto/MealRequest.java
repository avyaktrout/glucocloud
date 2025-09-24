package com.glucocloud.api.dto;

import com.glucocloud.api.entity.Meal;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MealRequest {

    @NotBlank(message = "Meal description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 0, message = "Carbs must be non-negative")
    @Max(value = 1000, message = "Carbs cannot exceed 1000g")
    private Integer carbsGrams;

    @Min(value = 0, message = "Calories must be non-negative")
    @Max(value = 10000, message = "Calories cannot exceed 10,000")
    private Integer calories;

    @Min(value = 0, message = "Protein must be non-negative")
    @Max(value = 500, message = "Protein cannot exceed 500g")
    private Integer proteinGrams;

    @Min(value = 0, message = "Fat must be non-negative")
    @Max(value = 500, message = "Fat cannot exceed 500g")
    private Integer fatGrams;

    private Meal.MealType mealType;

    @NotNull(message = "Meal timestamp is required")
    private LocalDateTime consumedAt;

    @Size(max = 1000, message = "Photo URL cannot exceed 1000 characters")
    private String photoUrl;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}