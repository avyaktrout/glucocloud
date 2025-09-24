package com.glucocloud.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description", nullable = false, length = 500)
    @NotBlank(message = "Meal description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Column(name = "carbs_grams")
    @Min(value = 0, message = "Carbs must be non-negative")
    @Max(value = 1000, message = "Carbs cannot exceed 1000g")
    private Integer carbsGrams;

    @Column(name = "calories")
    @Min(value = 0, message = "Calories must be non-negative")
    @Max(value = 10000, message = "Calories cannot exceed 10,000")
    private Integer calories;

    @Column(name = "protein_grams")
    @Min(value = 0, message = "Protein must be non-negative")
    @Max(value = 500, message = "Protein cannot exceed 500g")
    private Integer proteinGrams;

    @Column(name = "fat_grams")
    @Min(value = 0, message = "Fat must be non-negative")
    @Max(value = 500, message = "Fat cannot exceed 500g")
    private Integer fatGrams;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType;

    @Column(name = "consumed_at", nullable = false)
    @NotNull(message = "Meal timestamp is required")
    private LocalDateTime consumedAt;

    @Column(name = "photo_url", length = 1000)
    @Size(max = 1000, message = "Photo URL cannot exceed 1000 characters")
    private String photoUrl;

    @Column(name = "notes", length = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK,
        OTHER
    }

    // Helper methods for analytics
    public boolean isHighCarb() {
        return carbsGrams != null && carbsGrams > 45; // >45g considered high carb
    }

    public boolean isLowCarb() {
        return carbsGrams != null && carbsGrams < 15; // <15g considered low carb
    }

    public double getCarbRatio() {
        if (calories == null || calories == 0) {
            return 0.0;
        }
        return carbsGrams != null ? (carbsGrams * 4.0) / calories : 0.0; // 4 calories per gram of carbs
    }
}