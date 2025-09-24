package com.glucocloud.api.service;

import com.glucocloud.api.dto.*;
import com.glucocloud.api.entity.*;
import com.glucocloud.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final GlucoseReadingRepository glucoseReadingRepository;
    private final MealRepository mealRepository;
    private final MedicationRepository medicationRepository;
    private final GlucoseReadingService glucoseReadingService;

    public List<MealGlucoseCorrelation> analyzeMealGlucoseCorrelations(User user, LocalDateTime startDate, LocalDateTime endDate) {
        // Default to last 14 days if no dates provided
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(14);
        }

        List<Meal> meals = mealRepository.findMealsForCorrelationAnalysis(user, startDate, endDate);
        List<GlucoseReading> glucoseReadings = glucoseReadingRepository
                .findByUserAndTakenAtBetweenOrderByTakenAtDesc(user, startDate, endDate);

        List<MealGlucoseCorrelation> correlations = new ArrayList<>();

        for (Meal meal : meals) {
            MealGlucoseCorrelation correlation = analyzeIndividualMealImpact(meal, glucoseReadings);
            if (correlation != null) {
                correlations.add(correlation);
            }
        }

        return correlations.stream()
                .sorted((a, b) -> b.getMealTime().compareTo(a.getMealTime()))
                .collect(Collectors.toList());
    }

    private MealGlucoseCorrelation analyzeIndividualMealImpact(Meal meal, List<GlucoseReading> allReadings) {
        // Find glucose readings before and after the meal
        GlucoseReading preReading = findNearestGlucoseReading(meal.getConsumedAt(), allReadings, true, 120); // 2 hours before
        GlucoseReading postReading = findNearestGlucoseReading(meal.getConsumedAt(), allReadings, false, 180); // 3 hours after

        MealGlucoseCorrelation.MealGlucoseCorrelationBuilder builder = MealGlucoseCorrelation.builder()
                .mealId(meal.getId())
                .mealDescription(meal.getDescription())
                .carbsGrams(meal.getCarbsGrams())
                .mealTime(meal.getConsumedAt());

        if (preReading != null) {
            builder.preGlucoseValue(preReading.getReadingValue())
                    .preGlucoseTime(preReading.getTakenAt());
        }

        if (postReading != null) {
            builder.postGlucoseValue(postReading.getReadingValue())
                    .postGlucoseTime(postReading.getTakenAt());
        }

        // Calculate impact if we have both readings
        if (preReading != null && postReading != null) {
            BigDecimal glucoseRise = postReading.getReadingValue().subtract(preReading.getReadingValue());
            builder.glucoseRise(glucoseRise);

            // Calculate carb-to-glucose ratio
            if (meal.getCarbsGrams() != null && meal.getCarbsGrams() > 0) {
                double carbToGlucoseRatio = glucoseRise.doubleValue() / meal.getCarbsGrams();
                builder.carbToGlucoseRatio(Math.round(carbToGlucoseRatio * 100.0) / 100.0);
            }

            // Calculate minutes to peak
            long minutesToPeak = ChronoUnit.MINUTES.between(meal.getConsumedAt(), postReading.getTakenAt());
            builder.minutesToPeak((int) minutesToPeak);

            // Determine impact level
            String impact = determineGlucoseImpact(glucoseRise, meal.getCarbsGrams());
            builder.impact(impact);

            return builder.build();
        } else if (preReading != null || postReading != null) {
            // Partial data
            builder.impact("INSUFFICIENT_DATA");
            return builder.build();
        }

        return null; // No glucose data available
    }

    private GlucoseReading findNearestGlucoseReading(LocalDateTime mealTime, List<GlucoseReading> readings,
                                                    boolean before, int maxMinutes) {
        return readings.stream()
                .filter(reading -> {
                    if (before) {
                        return reading.getTakenAt().isBefore(mealTime) &&
                               ChronoUnit.MINUTES.between(reading.getTakenAt(), mealTime) <= maxMinutes;
                    } else {
                        return reading.getTakenAt().isAfter(mealTime) &&
                               ChronoUnit.MINUTES.between(mealTime, reading.getTakenAt()) <= maxMinutes;
                    }
                })
                .min((a, b) -> {
                    long diffA = Math.abs(ChronoUnit.MINUTES.between(a.getTakenAt(), mealTime));
                    long diffB = Math.abs(ChronoUnit.MINUTES.between(b.getTakenAt(), mealTime));
                    return Long.compare(diffA, diffB);
                })
                .orElse(null);
    }

    private String determineGlucoseImpact(BigDecimal glucoseRise, Integer carbsGrams) {
        double rise = glucoseRise.doubleValue();

        if (rise > 50) {
            return "HIGH_IMPACT";
        } else if (rise > 20) {
            return "MODERATE_IMPACT";
        } else if (rise >= 0) {
            return "LOW_IMPACT";
        } else {
            return "NEGATIVE_IMPACT"; // Glucose went down after meal
        }
    }

    public MealSummaryResponse generateMealSummary(User user, LocalDateTime startDate, LocalDateTime endDate) {
        // Default to last 30 days if no dates provided
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        List<Meal> meals = mealRepository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(user, startDate, endDate);

        if (meals.isEmpty()) {
            return MealSummaryResponse.builder()
                    .totalMeals(0)
                    .fromDate(startDate)
                    .toDate(endDate)
                    .nutritionBalance("INSUFFICIENT_DATA")
                    .build();
        }

        // Calculate basic stats
        int totalMeals = meals.size();
        List<Integer> carbsList = meals.stream().map(Meal::getCarbsGrams).filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> caloriesList = meals.stream().map(Meal::getCalories).filter(Objects::nonNull).collect(Collectors.toList());

        Integer avgCarbs = carbsList.isEmpty() ? null : (int) carbsList.stream().mapToInt(Integer::intValue).average().orElse(0);
        Integer avgCalories = caloriesList.isEmpty() ? null : (int) caloriesList.stream().mapToInt(Integer::intValue).average().orElse(0);
        Integer totalCarbs = carbsList.stream().mapToInt(Integer::intValue).sum();
        Integer totalCalories = caloriesList.stream().mapToInt(Integer::intValue).sum();

        // Count meal types
        Map<Meal.MealType, Long> mealTypeCounts = meals.stream()
                .filter(meal -> meal.getMealType() != null)
                .collect(Collectors.groupingBy(Meal::getMealType, Collectors.counting()));

        // Analyze carb patterns
        long highCarbMeals = meals.stream().filter(Meal::isHighCarb).count();
        long lowCarbMeals = meals.stream().filter(Meal::isLowCarb).count();

        // Calculate nutrition balance
        double avgCarbRatio = meals.stream()
                .filter(meal -> meal.getCarbRatio() > 0)
                .mapToDouble(Meal::getCarbRatio)
                .average()
                .orElse(0.0);

        String nutritionBalance = determineNutritionBalance(avgCarbRatio, highCarbMeals, totalMeals);

        return MealSummaryResponse.builder()
                .totalMeals(totalMeals)
                .averageCarbsPerMeal(avgCarbs)
                .averageCaloriesPerMeal(avgCalories)
                .totalCarbs(totalCarbs)
                .totalCalories(totalCalories)
                .highCarbMeals((int) highCarbMeals)
                .lowCarbMeals((int) lowCarbMeals)
                .highCarbPercentage(Math.round((double) highCarbMeals / totalMeals * 100.0 * 100.0) / 100.0)
                .lowCarbPercentage(Math.round((double) lowCarbMeals / totalMeals * 100.0 * 100.0) / 100.0)
                .breakfastCount(mealTypeCounts.getOrDefault(Meal.MealType.BREAKFAST, 0L).intValue())
                .lunchCount(mealTypeCounts.getOrDefault(Meal.MealType.LUNCH, 0L).intValue())
                .dinnerCount(mealTypeCounts.getOrDefault(Meal.MealType.DINNER, 0L).intValue())
                .snackCount(mealTypeCounts.getOrDefault(Meal.MealType.SNACK, 0L).intValue())
                .fromDate(startDate)
                .toDate(endDate)
                .nutritionBalance(nutritionBalance)
                .avgCarbRatio(Math.round(avgCarbRatio * 100.0) / 100.0)
                .build();
    }

    private String determineNutritionBalance(double avgCarbRatio, long highCarbMeals, int totalMeals) {
        if (totalMeals < 5) {
            return "INSUFFICIENT_DATA";
        }

        double highCarbPercentage = (double) highCarbMeals / totalMeals;

        if (avgCarbRatio > 0.6 || highCarbPercentage > 0.5) {
            return "HIGH_CARB";
        } else if (avgCarbRatio < 0.3 || highCarbPercentage < 0.2) {
            return "LOW_CARB";
        } else {
            return "BALANCED";
        }
    }

    public MedicationSummaryResponse generateMedicationSummary(User user, LocalDateTime startDate, LocalDateTime endDate) {
        // Default to last 30 days if no dates provided
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        List<Medication> medications = medicationRepository.findByUserAndTakenAtBetweenOrderByTakenAtDesc(user, startDate, endDate);

        if (medications.isEmpty()) {
            return MedicationSummaryResponse.builder()
                    .totalMedications(0)
                    .fromDate(startDate)
                    .toDate(endDate)
                    .adherenceEstimate("INSUFFICIENT_DATA")
                    .build();
        }

        int totalMedications = medications.size();
        long insulinDoses = medications.stream().filter(Medication::isInsulin).count();
        long oralMedications = medications.stream().filter(Medication::isOralMedication).count();
        long injectableMedications = medications.stream().filter(Medication::isInjectable).count();

        // Calculate effectiveness
        Double avgEffectiveness = medications.stream()
                .filter(med -> med.getEffectivenessRating() != null)
                .mapToInt(Medication::getEffectivenessRating)
                .average()
                .orElse(0.0);

        long medsWithSideEffects = medications.stream()
                .filter(med -> med.getSideEffects() != null && !med.getSideEffects().trim().isEmpty())
                .count();

        // Calculate adherence estimate
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        double medicationsPerDay = daysBetween > 0 ? (double) totalMedications / daysBetween : 0;
        String adherenceEstimate = estimateAdherence(medicationsPerDay, medications);

        // Get unique medication names
        List<String> uniqueNames = medications.stream()
                .map(Medication::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Find most common medication type
        String mostCommonType = medications.stream()
                .filter(med -> med.getMedicationType() != null)
                .collect(Collectors.groupingBy(Medication::getMedicationType, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().toString())
                .orElse("UNKNOWN");

        return MedicationSummaryResponse.builder()
                .totalMedications(totalMedications)
                .insulinDoses((int) insulinDoses)
                .oralMedications((int) oralMedications)
                .injectableMedications((int) injectableMedications)
                .averageEffectivenessRating(avgEffectiveness > 0 ? Math.round(avgEffectiveness * 100.0) / 100.0 : null)
                .medicationsWithSideEffects((int) medsWithSideEffects)
                .sideEffectsPercentage(Math.round((double) medsWithSideEffects / totalMedications * 100.0 * 100.0) / 100.0)
                .adherenceEstimate(adherenceEstimate)
                .medicationsPerDay(Math.round(medicationsPerDay * 100.0) / 100.0)
                .uniqueMedicationNames(uniqueNames)
                .uniqueMedicationCount(uniqueNames.size())
                .fromDate(startDate)
                .toDate(endDate)
                .mostCommonMedicationType(mostCommonType)
                .missedDosesEstimate(0) // Would require more complex logic
                .build();
    }

    private String estimateAdherence(double medicationsPerDay, List<Medication> medications) {
        if (medications.size() < 7) {
            return "INSUFFICIENT_DATA";
        }

        // Simple heuristic based on frequency
        if (medicationsPerDay >= 2.0) {
            return "EXCELLENT";
        } else if (medicationsPerDay >= 1.0) {
            return "GOOD";
        } else {
            return "POOR";
        }
    }
}