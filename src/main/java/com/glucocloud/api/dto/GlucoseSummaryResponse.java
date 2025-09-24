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
public class GlucoseSummaryResponse {

    private int totalReadings;
    private BigDecimal averageReading;
    private BigDecimal minReading;
    private BigDecimal maxReading;

    // Time in range statistics
    private int readingsInRange;
    private int readingsHigh;
    private int readingsLow;
    private double timeInRangePercentage;
    private double timeHighPercentage;
    private double timeLowPercentage;

    // Critical readings
    private int criticallyHighReadings;
    private int criticallyLowReadings;

    // Time period
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    // Recent trend (last 7 days vs previous 7 days)
    private String trend; // "IMPROVING", "STABLE", "WORSENING"
    private BigDecimal trendChange;
}