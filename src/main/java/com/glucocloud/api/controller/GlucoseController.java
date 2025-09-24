package com.glucocloud.api.controller;

import com.glucocloud.api.dto.GlucoseReadingRequest;
import com.glucocloud.api.dto.GlucoseReadingResponse;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.security.JwtUtils;
import com.glucocloud.api.service.GlucoseReadingService;
import com.glucocloud.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/glucose")
@RequiredArgsConstructor
public class GlucoseController {

    private final GlucoseReadingService glucoseReadingService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> createReading(
            @Valid @RequestBody GlucoseReadingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            GlucoseReadingResponse response = glucoseReadingService.createReading(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse("Failed to create glucose reading: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getReadings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            List<GlucoseReadingResponse> readings = glucoseReadingService.getUserReadings(user, from, to);
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve glucose readings: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReading(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            return glucoseReadingService.getReadingById(user, id)
                    .map(reading -> ResponseEntity.ok(reading))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve glucose reading: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReading(
            @PathVariable UUID id,
            @Valid @RequestBody GlucoseReadingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            GlucoseReadingResponse response = glucoseReadingService.updateReading(user, id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return createErrorResponse("Failed to update glucose reading: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Failed to update glucose reading: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReading(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            glucoseReadingService.deleteReading(user, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return createErrorResponse("Failed to delete glucose reading: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Failed to delete glucose reading: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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