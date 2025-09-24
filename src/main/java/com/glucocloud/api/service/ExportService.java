package com.glucocloud.api.service;

import com.glucocloud.api.entity.*;
import com.glucocloud.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportService {

    private final GlucoseReadingRepository glucoseReadingRepository;
    private final MealRepository mealRepository;
    private final MedicationRepository medicationRepository;
    private final DashboardService dashboardService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportGlucoseReadingsToCSV(User user, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        List<GlucoseReading> readings;

        if (startDate != null && endDate != null) {
            readings = glucoseReadingRepository.findByUserAndTakenAtBetweenOrderByTakenAtDesc(user, startDate, endDate);
        } else {
            readings = glucoseReadingRepository.findByUserOrderByTakenAtDesc(user);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Date", "Time", "Glucose (mg/dL)", "Reading Type", "Status", "In Range", "Note", "Created At"))) {

            for (GlucoseReading reading : readings) {
                String status = determineGlucoseStatus(reading);

                csvPrinter.printRecord(
                        reading.getTakenAt().toLocalDate().toString(),
                        reading.getTakenAt().toLocalTime().toString(),
                        reading.getReadingValue(),
                        reading.getReadingType() != null ? reading.getReadingType().toString() : "",
                        status,
                        reading.isInNormalRange() ? "Yes" : "No",
                        reading.getNote() != null ? reading.getNote() : "",
                        reading.getCreatedAt().format(DATE_TIME_FORMATTER)
                );
            }
        }

        return out.toByteArray();
    }

    public byte[] exportMealsToCSV(User user, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        List<Meal> meals;

        if (startDate != null && endDate != null) {
            meals = mealRepository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(user, startDate, endDate);
        } else {
            meals = mealRepository.findByUserOrderByConsumedAtDesc(user);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Date", "Time", "Description", "Meal Type", "Carbs (g)", "Calories",
                           "Protein (g)", "Fat (g)", "Carb Category", "Notes", "Photo URL", "Created At"))) {

            for (Meal meal : meals) {
                String carbCategory = determineCarbCategory(meal);

                csvPrinter.printRecord(
                        meal.getConsumedAt().toLocalDate().toString(),
                        meal.getConsumedAt().toLocalTime().toString(),
                        meal.getDescription(),
                        meal.getMealType() != null ? meal.getMealType().toString() : "",
                        meal.getCarbsGrams() != null ? meal.getCarbsGrams() : "",
                        meal.getCalories() != null ? meal.getCalories() : "",
                        meal.getProteinGrams() != null ? meal.getProteinGrams() : "",
                        meal.getFatGrams() != null ? meal.getFatGrams() : "",
                        carbCategory,
                        meal.getNotes() != null ? meal.getNotes() : "",
                        meal.getPhotoUrl() != null ? meal.getPhotoUrl() : "",
                        meal.getCreatedAt().format(DATE_TIME_FORMATTER)
                );
            }
        }

        return out.toByteArray();
    }

    public byte[] exportMedicationsToCSV(User user, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        List<Medication> medications;

        if (startDate != null && endDate != null) {
            medications = medicationRepository.findByUserAndTakenAtBetweenOrderByTakenAtDesc(user, startDate, endDate);
        } else {
            medications = medicationRepository.findByUserOrderByTakenAtDesc(user);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Date", "Time", "Medication Name", "Dosage", "Type", "Category",
                           "Effectiveness (1-5)", "Side Effects", "Notes", "Created At"))) {

            for (Medication medication : medications) {
                String category = determineMedicationCategory(medication);

                csvPrinter.printRecord(
                        medication.getTakenAt().toLocalDate().toString(),
                        medication.getTakenAt().toLocalTime().toString(),
                        medication.getName(),
                        medication.getDosage() != null ? medication.getDosage() : "",
                        medication.getMedicationType() != null ? medication.getMedicationType().toString() : "",
                        category,
                        medication.getEffectivenessRating() != null ? medication.getEffectivenessRating() : "",
                        medication.getSideEffects() != null ? medication.getSideEffects() : "",
                        medication.getNotes() != null ? medication.getNotes() : "",
                        medication.getCreatedAt().format(DATE_TIME_FORMATTER)
                );
            }
        }

        return out.toByteArray();
    }

    public byte[] exportComprehensiveHealthReport(User user, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        // Default to last 30 days if no dates provided
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            // Header section
            csvPrinter.printRecord("GlucoCloud Health Report");
            csvPrinter.printRecord("Generated on:", LocalDateTime.now().format(DATE_TIME_FORMATTER));
            csvPrinter.printRecord("Report Period:", startDate.toLocalDate() + " to " + endDate.toLocalDate());
            csvPrinter.printRecord("User:", user.getEmail());
            csvPrinter.printRecord(""); // Empty line

            // Get comprehensive dashboard data
            var dashboard = dashboardService.generateComprehensiveDashboard(user, startDate, endDate);

            // Health Score Section
            csvPrinter.printRecord("=== HEALTH OVERVIEW ===");
            csvPrinter.printRecord("Health Score:", dashboard.getHealthScore() + "/100");
            csvPrinter.printRecord("Health Status:", dashboard.getHealthScoreDescription());
            csvPrinter.printRecord(""); // Empty line

            // Glucose Summary
            var glucoseSummary = dashboard.getGlucoseSummary();
            csvPrinter.printRecord("=== GLUCOSE SUMMARY ===");
            csvPrinter.printRecord("Total Readings:", glucoseSummary.getTotalReadings());
            if (glucoseSummary.getAverageReading() != null) {
                csvPrinter.printRecord("Average Glucose:", glucoseSummary.getAverageReading() + " mg/dL");
                csvPrinter.printRecord("Min Glucose:", glucoseSummary.getMinReading() + " mg/dL");
                csvPrinter.printRecord("Max Glucose:", glucoseSummary.getMaxReading() + " mg/dL");
                csvPrinter.printRecord("Time in Range (70-180):", String.format("%.1f%%", glucoseSummary.getTimeInRangePercentage()));
                csvPrinter.printRecord("Time High (>180):", String.format("%.1f%%", glucoseSummary.getTimeHighPercentage()));
                csvPrinter.printRecord("Time Low (<70):", String.format("%.1f%%", glucoseSummary.getTimeLowPercentage()));
                csvPrinter.printRecord("Critical High Readings:", glucoseSummary.getCriticallyHighReadings());
                csvPrinter.printRecord("Critical Low Readings:", glucoseSummary.getCriticallyLowReadings());
                csvPrinter.printRecord("Trend:", glucoseSummary.getTrend());
            }
            csvPrinter.printRecord(""); // Empty line

            // Meal Summary
            var mealSummary = dashboard.getMealSummary();
            csvPrinter.printRecord("=== MEAL SUMMARY ===");
            csvPrinter.printRecord("Total Meals:", mealSummary.getTotalMeals());
            if (mealSummary.getAverageCarbsPerMeal() != null) {
                csvPrinter.printRecord("Average Carbs per Meal:", mealSummary.getAverageCarbsPerMeal() + "g");
                csvPrinter.printRecord("Average Calories per Meal:", mealSummary.getAverageCaloriesPerMeal());
                csvPrinter.printRecord("Total Carbs:", mealSummary.getTotalCarbs() + "g");
                csvPrinter.printRecord("High Carb Meals:", mealSummary.getHighCarbMeals() + " (" +
                    String.format("%.1f%%", mealSummary.getHighCarbPercentage()) + ")");
                csvPrinter.printRecord("Low Carb Meals:", mealSummary.getLowCarbMeals() + " (" +
                    String.format("%.1f%%", mealSummary.getLowCarbPercentage()) + ")");
                csvPrinter.printRecord("Nutrition Balance:", mealSummary.getNutritionBalance());
            }
            csvPrinter.printRecord(""); // Empty line

            // Medication Summary
            var medSummary = dashboard.getMedicationSummary();
            csvPrinter.printRecord("=== MEDICATION SUMMARY ===");
            csvPrinter.printRecord("Total Medications:", medSummary.getTotalMedications());
            csvPrinter.printRecord("Insulin Doses:", medSummary.getInsulinDoses());
            csvPrinter.printRecord("Oral Medications:", medSummary.getOralMedications());
            csvPrinter.printRecord("Injectable Medications:", medSummary.getInjectableMedications());
            if (medSummary.getAverageEffectivenessRating() != null) {
                csvPrinter.printRecord("Average Effectiveness:", String.format("%.1f/5", medSummary.getAverageEffectivenessRating()));
            }
            csvPrinter.printRecord("Medications with Side Effects:", medSummary.getMedicationsWithSideEffects() +
                " (" + String.format("%.1f%%", medSummary.getSideEffectsPercentage()) + ")");
            csvPrinter.printRecord("Adherence Estimate:", medSummary.getAdherenceEstimate());
            csvPrinter.printRecord(""); // Empty line

            // Insights
            csvPrinter.printRecord("=== KEY INSIGHTS ===");
            dashboard.getInsights().forEach((key, value) -> {
                try {
                    csvPrinter.printRecord(key + ":", value);
                } catch (IOException e) {
                    // Handle silently
                }
            });
            csvPrinter.printRecord(""); // Empty line

            // Recommendations
            csvPrinter.printRecord("=== RECOMMENDATIONS ===");
            dashboard.getRecommendations().forEach((key, value) -> {
                try {
                    csvPrinter.printRecord(key + ":", value);
                } catch (IOException e) {
                    // Handle silently
                }
            });

        }

        return out.toByteArray();
    }

    private String determineGlucoseStatus(GlucoseReading reading) {
        if (reading.isCriticallyHigh()) return "CRITICALLY_HIGH";
        if (reading.isCriticallyLow()) return "CRITICALLY_LOW";
        if (reading.isHigh()) return "HIGH";
        if (reading.isLow()) return "LOW";
        return "NORMAL";
    }

    private String determineCarbCategory(Meal meal) {
        if (meal.getCarbsGrams() == null) return "UNKNOWN";
        if (meal.isHighCarb()) return "HIGH_CARB";
        if (meal.isLowCarb()) return "LOW_CARB";
        return "MODERATE_CARB";
    }

    private String determineMedicationCategory(Medication medication) {
        if (medication.isInsulin()) return "INSULIN";
        if (medication.isOralMedication()) return "ORAL";
        if (medication.isInjectable()) return "INJECTABLE";
        return "OTHER";
    }
}