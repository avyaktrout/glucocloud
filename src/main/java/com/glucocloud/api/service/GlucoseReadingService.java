package com.glucocloud.api.service;

import com.glucocloud.api.dto.GlucoseReadingRequest;
import com.glucocloud.api.dto.GlucoseReadingResponse;
import com.glucocloud.api.dto.GlucoseSummaryResponse;
import com.glucocloud.api.entity.GlucoseReading;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.repository.GlucoseReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GlucoseReadingService {

    private final GlucoseReadingRepository glucoseReadingRepository;

    public GlucoseReadingResponse createReading(User user, GlucoseReadingRequest request) {
        GlucoseReading reading = GlucoseReading.builder()
                .user(user)
                .readingValue(request.getReadingValue())
                .takenAt(request.getTakenAt())
                .readingType(request.getReadingType())
                .note(request.getNote())
                .build();

        GlucoseReading savedReading = glucoseReadingRepository.save(reading);
        return GlucoseReadingResponse.fromEntity(savedReading);
    }

    @Transactional(readOnly = true)
    public List<GlucoseReadingResponse> getUserReadings(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<GlucoseReading> readings;

        if (startDate != null && endDate != null) {
            readings = glucoseReadingRepository.findByUserAndTakenAtBetweenOrderByTakenAtDesc(user, startDate, endDate);
        } else {
            readings = glucoseReadingRepository.findByUserOrderByTakenAtDesc(user);
        }

        return readings.stream()
                .map(GlucoseReadingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<GlucoseReadingResponse> getReadingById(User user, UUID readingId) {
        return glucoseReadingRepository.findByIdAndUser(readingId, user)
                .map(GlucoseReadingResponse::fromEntity);
    }

    public GlucoseReadingResponse updateReading(User user, UUID readingId, GlucoseReadingRequest request) {
        GlucoseReading reading = glucoseReadingRepository.findByIdAndUser(readingId, user)
                .orElseThrow(() -> new RuntimeException("Glucose reading not found"));

        reading.setReadingValue(request.getReadingValue());
        reading.setTakenAt(request.getTakenAt());
        reading.setReadingType(request.getReadingType());
        reading.setNote(request.getNote());

        GlucoseReading savedReading = glucoseReadingRepository.save(reading);
        return GlucoseReadingResponse.fromEntity(savedReading);
    }

    public void deleteReading(User user, UUID readingId) {
        GlucoseReading reading = glucoseReadingRepository.findByIdAndUser(readingId, user)
                .orElseThrow(() -> new RuntimeException("Glucose reading not found"));

        glucoseReadingRepository.delete(reading);
    }

    @Transactional(readOnly = true)
    public GlucoseSummaryResponse getGlucoseSummary(User user, LocalDateTime startDate, LocalDateTime endDate) {
        // Default to last 30 days if no dates provided
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        long totalReadings = glucoseReadingRepository.countByUserAndDateRange(user, startDate, endDate);

        if (totalReadings == 0) {
            return GlucoseSummaryResponse.builder()
                    .totalReadings(0)
                    .fromDate(startDate)
                    .toDate(endDate)
                    .build();
        }

        Double avgReading = glucoseReadingRepository.findAverageByUserAndDateRange(user, startDate, endDate);
        Double minReading = glucoseReadingRepository.findMinByUserAndDateRange(user, startDate, endDate);
        Double maxReading = glucoseReadingRepository.findMaxByUserAndDateRange(user, startDate, endDate);

        long inRange = glucoseReadingRepository.countInRangeByUserAndDateRange(user, startDate, endDate);
        long high = glucoseReadingRepository.countHighByUserAndDateRange(user, startDate, endDate);
        long low = glucoseReadingRepository.countLowByUserAndDateRange(user, startDate, endDate);
        long criticallyHigh = glucoseReadingRepository.countCriticallyHighByUserAndDateRange(user, startDate, endDate);
        long criticallyLow = glucoseReadingRepository.countCriticallyLowByUserAndDateRange(user, startDate, endDate);

        double timeInRangePercentage = (double) inRange / totalReadings * 100;
        double timeHighPercentage = (double) high / totalReadings * 100;
        double timeLowPercentage = (double) low / totalReadings * 100;

        // Calculate trend (last 7 days vs previous 7 days)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        LocalDateTime twoWeeksAgo = now.minusDays(14);

        Double recentAvg = glucoseReadingRepository.findAverageByUserAndDateRange(user, weekAgo, now);
        Double previousAvg = glucoseReadingRepository.findAverageByUserAndDateRange(user, twoWeeksAgo, weekAgo);

        String trend = "STABLE";
        BigDecimal trendChange = BigDecimal.ZERO;

        if (recentAvg != null && previousAvg != null) {
            trendChange = BigDecimal.valueOf(recentAvg - previousAvg).setScale(2, RoundingMode.HALF_UP);
            if (trendChange.compareTo(BigDecimal.valueOf(10)) > 0) {
                trend = "WORSENING";
            } else if (trendChange.compareTo(BigDecimal.valueOf(-10)) < 0) {
                trend = "IMPROVING";
            }
        }

        return GlucoseSummaryResponse.builder()
                .totalReadings((int) totalReadings)
                .averageReading(avgReading != null ? BigDecimal.valueOf(avgReading).setScale(2, RoundingMode.HALF_UP) : null)
                .minReading(minReading != null ? BigDecimal.valueOf(minReading).setScale(2, RoundingMode.HALF_UP) : null)
                .maxReading(maxReading != null ? BigDecimal.valueOf(maxReading).setScale(2, RoundingMode.HALF_UP) : null)
                .readingsInRange((int) inRange)
                .readingsHigh((int) high)
                .readingsLow((int) low)
                .timeInRangePercentage(Math.round(timeInRangePercentage * 100.0) / 100.0)
                .timeHighPercentage(Math.round(timeHighPercentage * 100.0) / 100.0)
                .timeLowPercentage(Math.round(timeLowPercentage * 100.0) / 100.0)
                .criticallyHighReadings((int) criticallyHigh)
                .criticallyLowReadings((int) criticallyLow)
                .fromDate(startDate)
                .toDate(endDate)
                .trend(trend)
                .trendChange(trendChange)
                .build();
    }
}