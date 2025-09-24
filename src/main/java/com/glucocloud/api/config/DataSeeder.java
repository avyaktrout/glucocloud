package com.glucocloud.api.config;

import com.glucocloud.api.entity.*;
import com.glucocloud.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!production") // Only run in non-production environments
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GlucoseReadingRepository glucoseReadingRepository;
    private final MealRepository mealRepository;
    private final MedicationRepository medicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Seeding sample data...");
            createSampleData();
            log.info("Sample data created successfully!");
        }
    }

    private void createSampleData() {
        // Create demo user
        User demoUser = User.builder()
                .email("demo@glucocloud.com")
                .passwordHash(passwordEncoder.encode("demo123"))
                .firstName("Demo")
                .lastName("User")
                .isActive(true)
                .build();

        demoUser = userRepository.save(demoUser);

        // Generate sample glucose readings for the last 30 days
        List<GlucoseReading> readings = generateSampleReadings(demoUser);
        glucoseReadingRepository.saveAll(readings);

        // Generate sample meals
        List<Meal> meals = generateSampleMeals(demoUser);
        mealRepository.saveAll(meals);

        // Generate sample medications
        List<Medication> medications = generateSampleMedications(demoUser);
        medicationRepository.saveAll(medications);

        log.info("Created demo user: demo@glucocloud.com / demo123");
        log.info("Generated {} sample glucose readings", readings.size());
        log.info("Generated {} sample meals", meals.size());
        log.info("Generated {} sample medications", medications.size());
    }

    private List<GlucoseReading> generateSampleReadings(User user) {
        List<GlucoseReading> readings = new ArrayList<>();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        // Generate readings for the last 30 days
        for (int day = 0; day < 30; day++) {
            LocalDateTime baseDate = now.minusDays(day);

            // Generate 3-5 readings per day at typical times
            int readingsPerDay = 3 + random.nextInt(3); // 3-5 readings

            for (int i = 0; i < readingsPerDay; i++) {
                LocalDateTime readingTime;
                GlucoseReading.ReadingType readingType;
                BigDecimal baseValue;

                // Simulate realistic reading times and values
                switch (i) {
                    case 0: // Morning fasting
                        readingTime = baseDate.withHour(7).withMinute(random.nextInt(60));
                        readingType = GlucoseReading.ReadingType.FASTING;
                        baseValue = BigDecimal.valueOf(80 + random.nextGaussian() * 15); // ~80-120
                        break;
                    case 1: // Before lunch
                        readingTime = baseDate.withHour(11).withMinute(30 + random.nextInt(30)); // 30-59 minutes
                        readingType = GlucoseReading.ReadingType.BEFORE_MEAL;
                        baseValue = BigDecimal.valueOf(90 + random.nextGaussian() * 20); // ~70-130
                        break;
                    case 2: // After dinner
                        readingTime = baseDate.withHour(19).withMinute(random.nextInt(60));
                        readingType = GlucoseReading.ReadingType.AFTER_MEAL;
                        baseValue = BigDecimal.valueOf(140 + random.nextGaussian() * 30); // ~110-200
                        break;
                    case 3: // Bedtime
                        readingTime = baseDate.withHour(22).withMinute(random.nextInt(60));
                        readingType = GlucoseReading.ReadingType.BEDTIME;
                        baseValue = BigDecimal.valueOf(110 + random.nextGaussian() * 25); // ~85-150
                        break;
                    default: // Random
                        readingTime = baseDate.withHour(random.nextInt(24)).withMinute(random.nextInt(60));
                        readingType = GlucoseReading.ReadingType.RANDOM;
                        baseValue = BigDecimal.valueOf(100 + random.nextGaussian() * 30); // ~70-160
                        break;
                }

                // Ensure reading is within realistic bounds
                BigDecimal finalValue = baseValue;
                if (finalValue.compareTo(BigDecimal.valueOf(40)) < 0) {
                    finalValue = BigDecimal.valueOf(40 + random.nextInt(20));
                }
                if (finalValue.compareTo(BigDecimal.valueOf(400)) > 0) {
                    finalValue = BigDecimal.valueOf(300 + random.nextInt(50));
                }

                // Round to 1 decimal place
                finalValue = finalValue.setScale(1, BigDecimal.ROUND_HALF_UP);

                // Add some realistic notes occasionally
                String note = null;
                if (random.nextDouble() < 0.3) { // 30% chance of having a note
                    String[] sampleNotes = {
                            "Before breakfast",
                            "Feeling good today",
                            "After exercise",
                            "Had a big meal",
                            "Feeling tired",
                            "Stressful day",
                            "Weekend reading"
                    };
                    note = sampleNotes[random.nextInt(sampleNotes.length)];
                }

                GlucoseReading reading = GlucoseReading.builder()
                        .user(user)
                        .readingValue(finalValue)
                        .takenAt(readingTime)
                        .readingType(readingType)
                        .note(note)
                        .build();

                readings.add(reading);
            }
        }

        return readings;
    }

    private List<Meal> generateSampleMeals(User user) {
        List<Meal> meals = new ArrayList<>();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        String[] breakfastMeals = {
            "Oatmeal with berries", "Greek yogurt with granola", "Whole wheat toast with avocado",
            "Scrambled eggs with spinach", "Cereal with milk", "Banana and peanut butter"
        };

        String[] lunchMeals = {
            "Grilled chicken salad", "Turkey sandwich", "Quinoa bowl with vegetables",
            "Soup and crackers", "Tuna salad wrap", "Stir-fry with brown rice"
        };

        String[] dinnerMeals = {
            "Baked salmon with vegetables", "Pasta with marinara sauce", "Grilled chicken breast",
            "Beef stir-fry", "Vegetable curry with rice", "Pork chops with sweet potato"
        };

        String[] snackMeals = {
            "Apple with almonds", "Cheese and crackers", "Trail mix",
            "Greek yogurt", "Carrot sticks with hummus", "Protein bar"
        };

        // Generate meals for the last 30 days
        for (int day = 0; day < 30; day++) {
            LocalDateTime baseDate = now.minusDays(day);

            // Breakfast
            if (random.nextDouble() < 0.8) { // 80% chance of breakfast
                String description = breakfastMeals[random.nextInt(breakfastMeals.length)];
                meals.add(createMeal(user, description, Meal.MealType.BREAKFAST,
                    baseDate.withHour(7).withMinute(30 + random.nextInt(30)),
                    25 + random.nextInt(20), // 25-45g carbs
                    250 + random.nextInt(200), // 250-450 calories
                    random));
            }

            // Lunch
            if (random.nextDouble() < 0.9) { // 90% chance of lunch
                String description = lunchMeals[random.nextInt(lunchMeals.length)];
                meals.add(createMeal(user, description, Meal.MealType.LUNCH,
                    baseDate.withHour(12).withMinute(random.nextInt(60)),
                    35 + random.nextInt(25), // 35-60g carbs
                    400 + random.nextInt(300), // 400-700 calories
                    random));
            }

            // Dinner
            if (random.nextDouble() < 0.95) { // 95% chance of dinner
                String description = dinnerMeals[random.nextInt(dinnerMeals.length)];
                meals.add(createMeal(user, description, Meal.MealType.DINNER,
                    baseDate.withHour(18).withMinute(30 + random.nextInt(30)),
                    40 + random.nextInt(30), // 40-70g carbs
                    500 + random.nextInt(400), // 500-900 calories
                    random));
            }

            // Snacks (1-2 per day)
            int snacksToday = random.nextInt(3); // 0-2 snacks
            for (int snack = 0; snack < snacksToday; snack++) {
                String description = snackMeals[random.nextInt(snackMeals.length)];
                meals.add(createMeal(user, description, Meal.MealType.SNACK,
                    baseDate.withHour(10 + random.nextInt(8)).withMinute(random.nextInt(60)),
                    10 + random.nextInt(20), // 10-30g carbs
                    100 + random.nextInt(200), // 100-300 calories
                    random));
            }
        }

        return meals;
    }

    private Meal createMeal(User user, String description, Meal.MealType mealType,
                           LocalDateTime consumedAt, int baseCarbs, int baseCalories, Random random) {
        return Meal.builder()
                .user(user)
                .description(description)
                .mealType(mealType)
                .consumedAt(consumedAt)
                .carbsGrams(baseCarbs)
                .calories(baseCalories)
                .proteinGrams(15 + random.nextInt(25)) // 15-40g protein
                .fatGrams(10 + random.nextInt(20)) // 10-30g fat
                .notes(random.nextDouble() < 0.3 ? "Delicious meal!" : null)
                .build();
    }

    private List<Medication> generateSampleMedications(User user) {
        List<Medication> medications = new ArrayList<>();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        // Common diabetes medications with realistic dosages
        String[][] medicationData = {
            {"Metformin", "500mg", "METFORMIN"},
            {"Humalog", "10 units", "INSULIN_RAPID"},
            {"Lantus", "20 units", "INSULIN_LONG"},
            {"Lisinopril", "10mg", "BLOOD_PRESSURE"},
            {"Atorvastatin", "20mg", "CHOLESTEROL"},
            {"Vitamin D3", "2000 IU", "SUPPLEMENT"}
        };

        // Generate medications for the last 30 days
        for (int day = 0; day < 30; day++) {
            LocalDateTime baseDate = now.minusDays(day);

            // Generate 2-4 medication entries per day
            int medicationsToday = 2 + random.nextInt(3);

            for (int med = 0; med < medicationsToday; med++) {
                String[] medData = medicationData[random.nextInt(medicationData.length)];

                // Vary timing throughout the day
                LocalDateTime takenAt = baseDate.withHour(8 + med * 4 + random.nextInt(2))
                    .withMinute(random.nextInt(60));

                Medication medication = Medication.builder()
                        .user(user)
                        .name(medData[0])
                        .dosage(medData[1])
                        .medicationType(Medication.MedicationType.valueOf(medData[2]))
                        .takenAt(takenAt)
                        .effectivenessRating(3 + random.nextInt(3)) // 3-5 rating
                        .notes(random.nextDouble() < 0.2 ? "Took with food" : null)
                        .sideEffects(random.nextDouble() < 0.1 ? "Mild stomach upset" : null)
                        .build();

                medications.add(medication);
            }
        }

        return medications;
    }
}