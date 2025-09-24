package com.glucocloud.api.controller;

import com.glucocloud.api.dto.MealRequest;
import com.glucocloud.api.dto.MealResponse;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.security.JwtUtils;
import com.glucocloud.api.service.MealService;
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
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> createMeal(
            @Valid @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            MealResponse response = mealService.createMeal(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse("Failed to create meal: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getMeals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            List<MealResponse> meals = mealService.getUserMeals(user, from, to);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve meals: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeal(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            return mealService.getMealById(user, id)
                    .map(meal -> ResponseEntity.ok(meal))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve meal: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeal(
            @PathVariable UUID id,
            @Valid @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            MealResponse response = mealService.updateMeal(user, id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return createErrorResponse("Failed to update meal: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Failed to update meal: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeal(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            User user = getCurrentUser(authHeader);
            mealService.deleteMeal(user, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return createErrorResponse("Failed to delete meal: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Failed to delete meal: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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