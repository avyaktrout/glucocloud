package com.glucocloud.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "glucose_readings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlucoseReading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reading_value", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Glucose reading value is required")
    @DecimalMin(value = "20.0", message = "Glucose reading must be at least 20 mg/dL")
    @DecimalMax(value = "999.99", message = "Glucose reading must be less than 1000 mg/dL")
    private BigDecimal readingValue;

    @Column(name = "taken_at", nullable = false)
    @NotNull(message = "Reading timestamp is required")
    private LocalDateTime takenAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_type")
    private ReadingType readingType;

    @Column(name = "note", length = 500)
    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum ReadingType {
        FASTING,
        BEFORE_MEAL,
        AFTER_MEAL,
        BEDTIME,
        RANDOM,
        OTHER
    }

    // Helper methods for analytics
    public boolean isInNormalRange() {
        // Normal range: 70-180 mg/dL (general guideline)
        return readingValue.compareTo(BigDecimal.valueOf(70)) >= 0 &&
               readingValue.compareTo(BigDecimal.valueOf(180)) <= 0;
    }

    public boolean isHigh() {
        return readingValue.compareTo(BigDecimal.valueOf(180)) > 0;
    }

    public boolean isLow() {
        return readingValue.compareTo(BigDecimal.valueOf(70)) < 0;
    }

    public boolean isCriticallyHigh() {
        return readingValue.compareTo(BigDecimal.valueOf(250)) > 0;
    }

    public boolean isCriticallyLow() {
        return readingValue.compareTo(BigDecimal.valueOf(54)) < 0;
    }
}