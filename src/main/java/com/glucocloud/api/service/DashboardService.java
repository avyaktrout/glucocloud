package com.glucocloud.api.service;

import com.glucocloud.api.dto.*;
import com.glucocloud.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final GlucoseReadingService glucoseReadingService;
    private final AnalyticsService analyticsService;

    public ComprehensiveDashboard generateComprehensiveDashboard(User user, LocalDateTime startDate, LocalDateTime endDate) {
        // Default to last 30 days if no dates provided
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        // Generate all summaries
        GlucoseSummaryResponse glucoseSummary = glucoseReadingService.getGlucoseSummary(user, startDate, endDate);
        MealSummaryResponse mealSummary = analyticsService.generateMealSummary(user, startDate, endDate);
        MedicationSummaryResponse medicationSummary = analyticsService.generateMedicationSummary(user, startDate, endDate);

        // Generate correlations and insights
        List<MealGlucoseCorrelation> correlations = analyticsService.analyzeMealGlucoseCorrelations(user, startDate, endDate);
        Map<String, String> insights = generateInsights(glucoseSummary, mealSummary, medicationSummary, correlations);
        Map<String, String> recommendations = generateRecommendations(glucoseSummary, mealSummary, medicationSummary, correlations);

        // Calculate health score
        int healthScore = calculateHealthScore(glucoseSummary, mealSummary, medicationSummary);
        String healthScoreDescription = getHealthScoreDescription(healthScore);

        // Generate progress metrics
        Map<String, Object> progressMetrics = generateProgressMetrics(user, startDate, endDate, glucoseSummary);

        return ComprehensiveDashboard.builder()
                .generatedAt(LocalDateTime.now())
                .fromDate(startDate)
                .toDate(endDate)
                .glucoseSummary(glucoseSummary)
                .mealSummary(mealSummary)
                .medicationSummary(medicationSummary)
                .mealGlucoseCorrelations(correlations)
                .insights(insights)
                .recommendations(recommendations)
                .healthScore(healthScore)
                .healthScoreDescription(healthScoreDescription)
                .progressMetrics(progressMetrics)
                .build();
    }

    private Map<String, String> generateInsights(GlucoseSummaryResponse glucose, MealSummaryResponse meals,
                                               MedicationSummaryResponse medications, List<MealGlucoseCorrelation> correlations) {
        Map<String, String> insights = new HashMap<>();

        // Glucose insights
        if (glucose.getTotalReadings() > 0) {
            insights.put("GLUCOSE_PATTERN",
                String.format("Your time in range is %.1f%%. Target is >70%%.", glucose.getTimeInRangePercentage()));

            if ("IMPROVING".equals(glucose.getTrend())) {
                insights.put("GLUCOSE_TREND", "Your glucose control has improved over the last week! ✨");
            } else if ("WORSENING".equals(glucose.getTrend())) {
                insights.put("GLUCOSE_TREND", "Your glucose readings have been trending higher recently.");
            }
        }

        // Meal insights
        if (meals.getTotalMeals() > 0) {
            insights.put("MEAL_PATTERN",
                String.format("You logged %d meals with an average of %d carbs per meal.",
                meals.getTotalMeals(), meals.getAverageCarbsPerMeal()));

            if ("HIGH_CARB".equals(meals.getNutritionBalance())) {
                insights.put("NUTRITION_BALANCE", "Your recent meals tend to be high in carbohydrates.");
            } else if ("LOW_CARB".equals(meals.getNutritionBalance())) {
                insights.put("NUTRITION_BALANCE", "You're following a low-carb eating pattern.");
            } else if ("BALANCED".equals(meals.getNutritionBalance())) {
                insights.put("NUTRITION_BALANCE", "Your meals show a balanced carb distribution. 👍");
            }
        }

        // Medication insights
        if (medications.getTotalMedications() > 0) {
            if ("EXCELLENT".equals(medications.getAdherenceEstimate())) {
                insights.put("MEDICATION_ADHERENCE", "Excellent medication tracking! You're very consistent. 🎯");
            } else if ("GOOD".equals(medications.getAdherenceEstimate())) {
                insights.put("MEDICATION_ADHERENCE", "Good medication tracking. Keep it up!");
            }

            if (medications.getAverageEffectivenessRating() != null && medications.getAverageEffectivenessRating() >= 4.0) {
                insights.put("MEDICATION_EFFECTIVENESS", "Your medications are working well based on your ratings.");
            }
        }

        // Correlation insights
        long highImpactMeals = correlations.stream()
                .filter(c -> "HIGH_IMPACT".equals(c.getImpact()))
                .count();

        if (highImpactMeals > 0) {
            insights.put("MEAL_IMPACT",
                String.format("Found %d meals with high glucose impact. Consider reviewing carb content.", highImpactMeals));
        }

        return insights;
    }

    private Map<String, String> generateRecommendations(GlucoseSummaryResponse glucose, MealSummaryResponse meals,
                                                       MedicationSummaryResponse medications, List<MealGlucoseCorrelation> correlations) {
        Map<String, String> recommendations = new HashMap<>();

        // Glucose recommendations
        if (glucose.getTimeInRangePercentage() < 70.0) {
            recommendations.put("IMPROVE_TIME_IN_RANGE",
                "Consider reviewing meal timing and carb counting with your healthcare provider.");
        }

        if (glucose.getCriticallyHighReadings() > 0 || glucose.getCriticallyLowReadings() > 0) {
            recommendations.put("CRITICAL_READINGS",
                "You have critical readings. Please discuss with your healthcare provider immediately.");
        }

        // Meal recommendations
        if (meals.getTotalMeals() > 0) {
            double mealsPerDay = (double) meals.getTotalMeals() / 30; // Assuming 30-day period
            if (mealsPerDay < 2.0) {
                recommendations.put("INCREASE_MEAL_LOGGING",
                    "Try to log more meals for better glucose pattern analysis.");
            }

            if (meals.getHighCarbPercentage() > 60.0) {
                recommendations.put("REDUCE_HIGH_CARB_MEALS",
                    "Consider reducing high-carb meals to improve glucose control.");
            }
        }

        // Medication recommendations
        if (medications.getTotalMedications() > 0) {
            if ("POOR".equals(medications.getAdherenceEstimate())) {
                recommendations.put("IMPROVE_MEDICATION_TRACKING",
                    "Regular medication logging helps identify patterns and improve management.");
            }

            if (medications.getSideEffectsPercentage() > 20.0) {
                recommendations.put("REVIEW_SIDE_EFFECTS",
                    "Consider discussing medication side effects with your healthcare provider.");
            }
        }

        // Correlation-based recommendations
        List<MealGlucoseCorrelation> highImpactMeals = correlations.stream()
                .filter(c -> "HIGH_IMPACT".equals(c.getImpact()))
                .limit(3)
                .toList();

        if (!highImpactMeals.isEmpty()) {
            StringBuilder mealNames = new StringBuilder();
            for (MealGlucoseCorrelation meal : highImpactMeals) {
                if (mealNames.length() > 0) mealNames.append(", ");
                mealNames.append(meal.getMealDescription());
            }
            recommendations.put("HIGH_IMPACT_MEALS",
                String.format("Consider moderating portions or timing for: %s", mealNames.toString()));
        }

        // General recommendations
        if (glucose.getTotalReadings() < 30) { // Less than 1 per day average
            recommendations.put("INCREASE_MONITORING",
                "More frequent glucose monitoring helps identify patterns and improve control.");
        }

        return recommendations;
    }

    private int calculateHealthScore(GlucoseSummaryResponse glucose, MealSummaryResponse meals, MedicationSummaryResponse medications) {
        int score = 50; // Base score

        // Glucose component (40% of score)
        if (glucose.getTotalReadings() > 0) {
            // Time in range (20 points max)
            score += (int) (glucose.getTimeInRangePercentage() / 100.0 * 20);

            // Low critical readings (10 points max)
            if (glucose.getCriticallyHighReadings() == 0 && glucose.getCriticallyLowReadings() == 0) {
                score += 10;
            } else if (glucose.getCriticallyHighReadings() + glucose.getCriticallyLowReadings() <= 2) {
                score += 5;
            }

            // Trend (10 points max)
            if ("IMPROVING".equals(glucose.getTrend())) {
                score += 10;
            } else if ("STABLE".equals(glucose.getTrend())) {
                score += 5;
            }
        }

        // Meal component (30% of score)
        if (meals.getTotalMeals() > 0) {
            // Nutrition balance (15 points max)
            if ("BALANCED".equals(meals.getNutritionBalance())) {
                score += 15;
            } else if ("LOW_CARB".equals(meals.getNutritionBalance())) {
                score += 10;
            } else if ("HIGH_CARB".equals(meals.getNutritionBalance())) {
                score += 5;
            }

            // Meal frequency (15 points max)
            double mealsPerDay = (double) meals.getTotalMeals() / 30;
            if (mealsPerDay >= 3.0) {
                score += 15;
            } else if (mealsPerDay >= 2.0) {
                score += 10;
            } else if (mealsPerDay >= 1.0) {
                score += 5;
            }
        }

        // Medication component (30% of score)
        if (medications.getTotalMedications() > 0) {
            // Adherence (15 points max)
            if ("EXCELLENT".equals(medications.getAdherenceEstimate())) {
                score += 15;
            } else if ("GOOD".equals(medications.getAdherenceEstimate())) {
                score += 10;
            } else if ("POOR".equals(medications.getAdherenceEstimate())) {
                score += 5;
            }

            // Effectiveness (15 points max)
            if (medications.getAverageEffectivenessRating() != null) {
                score += (int) (medications.getAverageEffectivenessRating() / 5.0 * 15);
            }
        }

        return Math.min(100, Math.max(0, score));
    }

    private String getHealthScoreDescription(int score) {
        if (score >= 90) {
            return "Excellent diabetes management! Keep up the great work! 🌟";
        } else if (score >= 80) {
            return "Very good diabetes management. Minor improvements could help. 👍";
        } else if (score >= 70) {
            return "Good diabetes management. Some areas for improvement. 📈";
        } else if (score >= 60) {
            return "Fair diabetes management. Focus on consistency. ⚖️";
        } else if (score >= 50) {
            return "Diabetes management needs attention. Consider healthcare provider consultation. ⚠️";
        } else {
            return "Diabetes management requires immediate attention. Please consult your healthcare provider. 🚨";
        }
    }

    private Map<String, Object> generateProgressMetrics(User user, LocalDateTime startDate, LocalDateTime endDate, GlucoseSummaryResponse currentSummary) {
        Map<String, Object> metrics = new HashMap<>();

        // Calculate previous period for comparison
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        LocalDateTime previousStart = startDate.minusDays(daysBetween);

        GlucoseSummaryResponse previousSummary = glucoseReadingService.getGlucoseSummary(user, previousStart, startDate);

        metrics.put("currentPeriodDays", daysBetween);
        metrics.put("currentReadingsCount", currentSummary.getTotalReadings());
        metrics.put("currentTimeInRange", currentSummary.getTimeInRangePercentage());

        if (previousSummary.getTotalReadings() > 0) {
            metrics.put("previousReadingsCount", previousSummary.getTotalReadings());
            metrics.put("previousTimeInRange", previousSummary.getTimeInRangePercentage());

            double timeInRangeChange = currentSummary.getTimeInRangePercentage() - previousSummary.getTimeInRangePercentage();
            metrics.put("timeInRangeChange", Math.round(timeInRangeChange * 100.0) / 100.0);

            if (timeInRangeChange > 5.0) {
                metrics.put("timeInRangeTrend", "IMPROVING");
            } else if (timeInRangeChange < -5.0) {
                metrics.put("timeInRangeTrend", "DECLINING");
            } else {
                metrics.put("timeInRangeTrend", "STABLE");
            }
        } else {
            metrics.put("timeInRangeTrend", "INSUFFICIENT_DATA");
        }

        return metrics;
    }
}