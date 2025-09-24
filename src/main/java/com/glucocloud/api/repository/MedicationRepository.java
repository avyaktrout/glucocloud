package com.glucocloud.api.repository;

import com.glucocloud.api.entity.Medication;
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
public interface MedicationRepository extends JpaRepository<Medication, UUID> {

    List<Medication> findByUserOrderByTakenAtDesc(User user);

    List<Medication> findByUserAndTakenAtBetweenOrderByTakenAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Medication> findByIdAndUser(UUID id, User user);

    @Query("SELECT COUNT(m) FROM Medication m WHERE m.user = :user AND m.takenAt BETWEEN :startDate AND :endDate")
    long countByUserAndDateRange(@Param("user") User user,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM Medication m WHERE m.user = :user AND m.takenAt BETWEEN :startDate AND :endDate AND " +
           "(m.medicationType = 'INSULIN_RAPID' OR m.medicationType = 'INSULIN_SHORT' OR " +
           "m.medicationType = 'INSULIN_INTERMEDIATE' OR m.medicationType = 'INSULIN_LONG')")
    long countInsulinDosesByUserAndDateRange(@Param("user") User user,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM Medication m WHERE m.user = :user AND m.takenAt BETWEEN :startDate AND :endDate AND " +
           "(m.medicationType = 'METFORMIN' OR m.medicationType = 'SULFONYLUREA' OR " +
           "m.medicationType = 'DPP4_INHIBITOR' OR m.medicationType = 'SGLT2_INHIBITOR')")
    long countOralMedicationsByUserAndDateRange(@Param("user") User user,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(m.effectivenessRating) FROM Medication m WHERE m.user = :user AND " +
           "m.takenAt BETWEEN :startDate AND :endDate AND m.effectivenessRating IS NOT NULL")
    Double findAverageEffectivenessRatingByUserAndDateRange(@Param("user") User user,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM Medication m WHERE m.user = :user AND m.takenAt BETWEEN :startDate AND :endDate AND " +
           "m.sideEffects IS NOT NULL AND LENGTH(m.sideEffects) > 0")
    long countMedicationsWithSideEffectsByUserAndDateRange(@Param("user") User user,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT m.name FROM Medication m WHERE m.user = :user ORDER BY m.name")
    List<String> findDistinctMedicationNamesByUser(@Param("user") User user);
}