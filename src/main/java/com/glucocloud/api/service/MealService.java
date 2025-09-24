package com.glucocloud.api.service;

import com.glucocloud.api.dto.MealRequest;
import com.glucocloud.api.dto.MealResponse;
import com.glucocloud.api.entity.Meal;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MealService {

    private final MealRepository mealRepository;

    public MealResponse createMeal(User user, MealRequest request) {
        Meal meal = Meal.builder()
                .user(user)
                .description(request.getDescription())
                .carbsGrams(request.getCarbsGrams())
                .calories(request.getCalories())
                .proteinGrams(request.getProteinGrams())
                .fatGrams(request.getFatGrams())
                .mealType(request.getMealType())
                .consumedAt(request.getConsumedAt())
                .photoUrl(request.getPhotoUrl())
                .notes(request.getNotes())
                .build();

        Meal savedMeal = mealRepository.save(meal);
        return MealResponse.fromEntity(savedMeal);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getUserMeals(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Meal> meals;

        if (startDate != null && endDate != null) {
            meals = mealRepository.findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(user, startDate, endDate);
        } else {
            meals = mealRepository.findByUserOrderByConsumedAtDesc(user);
        }

        return meals.stream()
                .map(MealResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MealResponse> getMealById(User user, UUID mealId) {
        return mealRepository.findByIdAndUser(mealId, user)
                .map(MealResponse::fromEntity);
    }

    public MealResponse updateMeal(User user, UUID mealId, MealRequest request) {
        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        meal.setDescription(request.getDescription());
        meal.setCarbsGrams(request.getCarbsGrams());
        meal.setCalories(request.getCalories());
        meal.setProteinGrams(request.getProteinGrams());
        meal.setFatGrams(request.getFatGrams());
        meal.setMealType(request.getMealType());
        meal.setConsumedAt(request.getConsumedAt());
        meal.setPhotoUrl(request.getPhotoUrl());
        meal.setNotes(request.getNotes());

        Meal savedMeal = mealRepository.save(meal);
        return MealResponse.fromEntity(savedMeal);
    }

    public void deleteMeal(User user, UUID mealId) {
        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        mealRepository.delete(meal);
    }

    @Transactional(readOnly = true)
    public List<Meal> getMealsForCorrelationAnalysis(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return mealRepository.findMealsForCorrelationAnalysis(user, startDate, endDate);
    }
}