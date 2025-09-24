package com.glucocloud.api.controller;

import com.glucocloud.api.dto.MedicationRequest;
import com.glucocloud.api.dto.MedicationResponse;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.security.JwtUtils;
import com.glucocloud.api.service.MedicationService;
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
@RequestMapping("/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> createMedication(
            @Valid @RequestBody MedicationRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            MedicationResponse response = medicationService.createMedication(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse("Failed to create medication: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getMedications(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            List<MedicationResponse> medications = medicationService.getUserMedications(user, from, to);
            return ResponseEntity.ok(medications);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve medications: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedication(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            return medicationService.getMedicationById(user, id)
                    .map(medication -> ResponseEntity.ok(medication))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve medication: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(
            @PathVariable UUID id,
            @Valid @RequestBody MedicationRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            MedicationResponse response = medicationService.updateMedication(user, id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return createErrorResponse("Failed to update medication: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Failed to update medication: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            medicationService.deleteMedication(user, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return createErrorResponse("Failed to delete medication: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Failed to delete medication: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/names")
    public ResponseEntity<?> getMedicationNames(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = getCurrentUser(authHeader);
            List<String> medicationNames = medicationService.getUserMedicationNames(user);
            return ResponseEntity.ok(medicationNames);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve medication names: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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