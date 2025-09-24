package com.glucocloud.api.controller;

import com.glucocloud.api.dto.*;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.security.JwtUtils;
import com.glucocloud.api.service.AnalyticsService;
import com.glucocloud.api.service.DashboardService;
import com.glucocloud.api.service.GlucoseReadingService;
import com.glucocloud.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final GlucoseReadingService glucoseReadingService;
    private final AnalyticsService analyticsService;
    private final DashboardService dashboardService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping("/glucose/summary")
    public ResponseEntity<?> getGlucoseSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            GlucoseSummaryResponse summary = glucoseReadingService.getGlucoseSummary(user, from, to);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve glucose summary: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/glucose/flags")
    public ResponseEntity<?> getGlucoseFlags(
            @RequestParam(required = false, defaultValue = "14") int days,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);

            GlucoseSummaryResponse summary = glucoseReadingService.getGlucoseSummary(user, startDate, endDate);

            Map<String, Object> flags = new HashMap<>();
            flags.put("period", days + " days");
            flags.put("fromDate", startDate);
            flags.put("toDate", endDate);

            // Generate flags based on patterns
            Map<String, String> alerts = new HashMap<>();

            if (summary.getCriticallyHighReadings() > 0) {
                alerts.put("CRITICAL_HIGH",
                    String.format("Found %d critically high readings (>250 mg/dL) in the last %d days",
                    summary.getCriticallyHighReadings(), days));
            }

            if (summary.getCriticallyLowReadings() > 0) {
                alerts.put("CRITICAL_LOW",
                    String.format("Found %d critically low readings (<54 mg/dL) in the last %d days",
                    summary.getCriticallyLowReadings(), days));
            }

            if (summary.getTimeInRangePercentage() < 70.0) {
                alerts.put("LOW_TIME_IN_RANGE",
                    String.format("Time in range is %.1f%% (target: >70%%)",
                    summary.getTimeInRangePercentage()));
            }

            if (summary.getTimeHighPercentage() > 25.0) {
                alerts.put("FREQUENT_HIGHS",
                    String.format("High readings %.1f%% of the time (target: <25%%)",
                    summary.getTimeHighPercentage()));
            }

            if (summary.getTimeLowPercentage() > 4.0) {
                alerts.put("FREQUENT_LOWS",
                    String.format("Low readings %.1f%% of the time (target: <4%%)",
                    summary.getTimeLowPercentage()));
            }

            if ("WORSENING".equals(summary.getTrend())) {
                alerts.put("WORSENING_TREND",
                    String.format("Average glucose increased by %.1f mg/dL over the last week",
                    summary.getTrendChange().doubleValue()));
            }

            // Frequency patterns
            double readingsPerDay = (double) summary.getTotalReadings() / days;
            if (readingsPerDay < 1.0) {
                alerts.put("LOW_MONITORING_FREQUENCY",
                    String.format("Only %.1f readings per day on average (recommended: 4+ per day)",
                    readingsPerDay));
            }

            flags.put("alerts", alerts);
            flags.put("alertCount", alerts.size());

            // Recommendations
            Map<String, String> recommendations = new HashMap<>();

            if (summary.getTimeInRangePercentage() < 70.0) {
                recommendations.put("TIME_IN_RANGE", "Consider reviewing diet and medication timing with your healthcare provider");
            }

            if (readingsPerDay < 2.0) {
                recommendations.put("MONITORING", "Increase monitoring frequency to better track patterns");
            }

            if (summary.getCriticallyHighReadings() > 0 || summary.getCriticallyLowReadings() > 0) {
                recommendations.put("MEDICAL_ATTENTION", "Contact your healthcare provider about critical readings");
            }

            flags.put("recommendations", recommendations);

            return ResponseEntity.ok(flags);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve glucose flags: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/meal-glucose-correlations")
    public ResponseEntity<?> getMealGlucoseCorrelations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            List<MealGlucoseCorrelation> correlations = analyticsService.analyzeMealGlucoseCorrelations(user, from, to);
            return ResponseEntity.ok(correlations);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve meal-glucose correlations: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/meals/summary")
    public ResponseEntity<?> getMealSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            MealSummaryResponse summary = analyticsService.generateMealSummary(user, from, to);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve meal summary: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/medications/summary")
    public ResponseEntity<?> getMedicationSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            MedicationSummaryResponse summary = analyticsService.generateMedicationSummary(user, from, to);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve medication summary: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getComprehensiveDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            ComprehensiveDashboard dashboard = dashboardService.generateComprehensiveDashboard(user, from, to);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return createErrorResponse("Failed to generate comprehensive dashboard: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private User getCurrentUser(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtils.getEmailFromJwtToken(token);
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }
}