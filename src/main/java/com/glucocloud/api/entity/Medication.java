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
@Table(name = "medications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "Medication name is required")
    @Size(max = 200, message = "Medication name cannot exceed 200 characters")
    private String name;

    @Column(name = "dosage", length = 100)
    @Size(max = 100, message = "Dosage cannot exceed 100 characters")
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(name = "medication_type")
    private MedicationType medicationType;

    @Column(name = "taken_at", nullable = false)
    @NotNull(message = "Medication timestamp is required")
    private LocalDateTime takenAt;

    @Column(name = "notes", length = 500)
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Column(name = "effectiveness_rating")
    @Min(value = 1, message = "Effectiveness rating must be between 1 and 5")
    @Max(value = 5, message = "Effectiveness rating must be between 1 and 5")
    private Integer effectivenessRating;

    @Column(name = "side_effects", length = 500)
    @Size(max = 500, message = "Side effects cannot exceed 500 characters")
    private String sideEffects;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MedicationType {
        INSULIN_RAPID,      // Rapid-acting insulin
        INSULIN_SHORT,      // Short-acting insulin
        INSULIN_INTERMEDIATE, // Intermediate-acting insulin
        INSULIN_LONG,       // Long-acting insulin
        METFORMIN,          // Metformin
        SULFONYLUREA,       // Sulfonylureas
        DPP4_INHIBITOR,     // DPP-4 inhibitors
        GLP1_AGONIST,       // GLP-1 agonists
        SGLT2_INHIBITOR,    // SGLT-2 inhibitors
        BLOOD_PRESSURE,     // Blood pressure medication
        CHOLESTEROL,        // Cholesterol medication
        SUPPLEMENT,         // Vitamins/supplements
        OTHER
    }

    // Helper methods for analytics
    public boolean isInsulin() {
        return medicationType != null && (
                medicationType == MedicationType.INSULIN_RAPID ||
                medicationType == MedicationType.INSULIN_SHORT ||
                medicationType == MedicationType.INSULIN_INTERMEDIATE ||
                medicationType == MedicationType.INSULIN_LONG
        );
    }

    public boolean isOralMedication() {
        return medicationType != null && (
                medicationType == MedicationType.METFORMIN ||
                medicationType == MedicationType.SULFONYLUREA ||
                medicationType == MedicationType.DPP4_INHIBITOR ||
                medicationType == MedicationType.SGLT2_INHIBITOR
        );
    }

    public boolean isInjectable() {
        return medicationType != null && (
                isInsulin() ||
                medicationType == MedicationType.GLP1_AGONIST
        );
    }
}