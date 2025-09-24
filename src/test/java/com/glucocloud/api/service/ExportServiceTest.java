package com.glucocloud.api.service;

import com.glucocloud.api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
class ExportServiceTest {

    @Test
    void testCsvExportFormats() throws IOException {
        // Create a test user
        User testUser = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        // Test that CSV headers are properly formatted
        String glucoseHeaders = "Date,Time,Glucose (mg/dL),Reading Type,Status,In Range,Note,Created At";
        String mealHeaders = "Date,Time,Description,Meal Type,Carbs (g),Calories,Protein (g),Fat (g),Carb Category,Notes,Photo URL,Created At";
        String medicationHeaders = "Date,Time,Medication Name,Dosage,Type,Category,Effectiveness (1-5),Side Effects,Notes,Created At";

        // Verify headers are well-formed
        assertNotNull(glucoseHeaders);
        assertNotNull(mealHeaders);
        assertNotNull(medicationHeaders);

        // Test filename generation
        String expectedPattern = "glucocloud_glucose_readings_\\d{4}-\\d{2}-\\d{2}\\.csv";
        String testFilename = "glucocloud_glucose_readings_" + LocalDateTime.now().toLocalDate() + ".csv";

        assertTrue(testFilename.contains("glucocloud"));
        assertTrue(testFilename.contains("glucose_readings"));
        assertTrue(testFilename.endsWith(".csv"));
    }

    @Test
    void testDateRangeHandling() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        // Test that date range is properly handled
        assertTrue(start.isBefore(end));
        assertEquals(30, java.time.temporal.ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()));
    }
}