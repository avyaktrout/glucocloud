package com.glucocloud.api.controller;

import com.glucocloud.api.entity.User;
import com.glucocloud.api.security.JwtUtils;
import com.glucocloud.api.service.ExportService;
import com.glucocloud.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping("/glucose")
    public ResponseEntity<?> exportGlucoseReadings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            byte[] csvData = exportService.exportGlucoseReadingsToCSV(user, from, to);

            String filename = generateFilename("glucose_readings", from, to);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(csvData.length)
                    .body(csvData);

        } catch (IOException e) {
            return createErrorResponse("Failed to export glucose readings: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return createErrorResponse("Failed to export glucose readings: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/meals")
    public ResponseEntity<?> exportMeals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            byte[] csvData = exportService.exportMealsToCSV(user, from, to);

            String filename = generateFilename("meals", from, to);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(csvData.length)
                    .body(csvData);

        } catch (IOException e) {
            return createErrorResponse("Failed to export meals: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return createErrorResponse("Failed to export meals: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/medications")
    public ResponseEntity<?> exportMedications(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            byte[] csvData = exportService.exportMedicationsToCSV(user, from, to);

            String filename = generateFilename("medications", from, to);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(csvData.length)
                    .body(csvData);

        } catch (IOException e) {
            return createErrorResponse("Failed to export medications: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return createErrorResponse("Failed to export medications: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/comprehensive-report")
    public ResponseEntity<?> exportComprehensiveReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            byte[] csvData = exportService.exportComprehensiveHealthReport(user, from, to);

            String filename = generateFilename("health_report", from, to);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(csvData.length)
                    .body(csvData);

        } catch (IOException e) {
            return createErrorResponse("Failed to export comprehensive report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return createErrorResponse("Failed to export comprehensive report: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all-data")
    public ResponseEntity<?> exportAllData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);

            // Create a ZIP file with all data
            Map<String, byte[]> files = new HashMap<>();
            files.put(generateFilename("glucose_readings", from, to), exportService.exportGlucoseReadingsToCSV(user, from, to));
            files.put(generateFilename("meals", from, to), exportService.exportMealsToCSV(user, from, to));
            files.put(generateFilename("medications", from, to), exportService.exportMedicationsToCSV(user, from, to));
            files.put(generateFilename("health_report", from, to), exportService.exportComprehensiveHealthReport(user, from, to));

            // For now, return the comprehensive report as the main export
            // In a full implementation, you'd create a ZIP file here
            String filename = generateFilename("glucocloud_data", from, to);
            byte[] csvData = files.get(generateFilename("health_report", from, to));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(csvData.length)
                    .body(csvData);

        } catch (IOException e) {
            return createErrorResponse("Failed to export all data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return createErrorResponse("Failed to export all data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/formats")
    public ResponseEntity<?> getAvailableFormats() {
        Map<String, Object> formats = new HashMap<>();
        formats.put("supported_formats", new String[]{"CSV"});
        formats.put("available_exports", new String[]{
            "glucose", "meals", "medications", "comprehensive-report", "all-data"
        });
        formats.put("description", "Export your health data for sharing with healthcare providers");
        formats.put("date_format", "ISO 8601 (e.g., 2024-01-15T08:30:00)");

        return ResponseEntity.ok(formats);
    }

    private String generateFilename(String dataType, LocalDateTime from, LocalDateTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateRange = "";

        if (from != null && to != null) {
            dateRange = "_" + from.format(formatter) + "_to_" + to.format(formatter);
        } else {
            dateRange = "_" + LocalDateTime.now().format(formatter);
        }

        return "glucocloud_" + dataType + dateRange + ".csv";
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