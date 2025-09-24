package com.glucocloud.api.repository;

import com.glucocloud.api.entity.Meal;
import com.glucocloud.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    List<Meal> findByUserOrderByConsumedAtDesc(User user);

    List<Meal> findByUserAndConsumedAtBetweenOrderByConsumedAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Meal> findByIdAndUser(UUID id, User user);

    @Query("SELECT COUNT(m) FROM Meal m WHERE m.user = :user AND m.consumedAt BETWEEN :startDate AND :endDate")
    long countByUserAndDateRange(@Param("user") User user,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(m.carbsGrams) FROM Meal m WHERE m.user = :user AND m.consumedAt BETWEEN :startDate AND :endDate AND m.carbsGrams IS NOT NULL")
    Double findAverageCarbsByUserAndDateRange(@Param("user") User user,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(m.calories) FROM Meal m WHERE m.user = :user AND m.consumedAt BETWEEN :startDate AND :endDate AND m.calories IS NOT NULL")
    Double findAverageCaloriesByUserAndDateRange(@Param("user") User user,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM Meal m WHERE m.user = :user AND m.consumedAt BETWEEN :startDate AND :endDate AND m.carbsGrams > 45")
    long countHighCarbMealsByUserAndDateRange(@Param("user") User user,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM Meal m WHERE m.user = :user AND m.consumedAt BETWEEN :startDate AND :endDate AND m.carbsGrams < 15")
    long countLowCarbMealsByUserAndDateRange(@Param("user") User user,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM Meal m WHERE m.user = :user AND m.consumedAt BETWEEN :startDate AND :endDate ORDER BY m.consumedAt")
    List<Meal> findMealsForCorrelationAnalysis(@Param("user") User user,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}