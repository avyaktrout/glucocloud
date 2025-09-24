package com.glucocloud.api.dto;

import com.glucocloud.api.entity.GlucoseReading;
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
public class GlucoseReadingResponse {

    private UUID id;
    private BigDecimal readingValue;
    private LocalDateTime takenAt;
    private GlucoseReading.ReadingType readingType;
    private String note;
    private LocalDateTime createdAt;

    // Analytics fields
    private String status; // "NORMAL", "HIGH", "LOW", "CRITICALLY_HIGH", "CRITICALLY_LOW"
    private boolean inRange;

    public static GlucoseReadingResponse fromEntity(GlucoseReading reading) {
        String status = "NORMAL";
        if (reading.isCriticallyHigh()) {
            status = "CRITICALLY_HIGH";
        } else if (reading.isCriticallyLow()) {
            status = "CRITICALLY_LOW";
        } else if (reading.isHigh()) {
            status = "HIGH";
        } else if (reading.isLow()) {
            status = "LOW";
        }

        return GlucoseReadingResponse.builder()
                .id(reading.getId())
                .readingValue(reading.getReadingValue())
                .takenAt(reading.getTakenAt())
                .readingType(reading.getReadingType())
                .note(reading.getNote())
                .createdAt(reading.getCreatedAt())
                .status(status)
                .inRange(reading.isInNormalRange())
                .build();
    }
}