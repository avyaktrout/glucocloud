package com.glucocloud.api.dto;

import com.glucocloud.api.entity.GlucoseReading;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GlucoseReadingRequest {

    @NotNull(message = "Glucose reading value is required")
    @DecimalMin(value = "20.0", message = "Glucose reading must be at least 20 mg/dL")
    @DecimalMax(value = "999.99", message = "Glucose reading must be less than 1000 mg/dL")
    private BigDecimal readingValue;

    @NotNull(message = "Reading timestamp is required")
    private LocalDateTime takenAt;

    private GlucoseReading.ReadingType readingType;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}