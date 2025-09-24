package com.glucocloud.api.repository;

import com.glucocloud.api.entity.GlucoseReading;
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
public interface GlucoseReadingRepository extends JpaRepository<GlucoseReading, UUID> {

    List<GlucoseReading> findByUserOrderByTakenAtDesc(User user);

    List<GlucoseReading> findByUserAndTakenAtBetweenOrderByTakenAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate);

    Optional<GlucoseReading> findByIdAndUser(UUID id, User user);

    @Query("SELECT COUNT(g) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate")
    long countByUserAndDateRange(@Param("user") User user,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(g.readingValue) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate")
    Double findAverageByUserAndDateRange(@Param("user") User user,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MIN(g.readingValue) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate")
    Double findMinByUserAndDateRange(@Param("user") User user,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MAX(g.readingValue) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate")
    Double findMaxByUserAndDateRange(@Param("user") User user,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(g) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate AND g.readingValue BETWEEN 70 AND 180")
    long countInRangeByUserAndDateRange(@Param("user") User user,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(g) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate AND g.readingValue > 180")
    long countHighByUserAndDateRange(@Param("user") User user,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(g) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate AND g.readingValue < 70")
    long countLowByUserAndDateRange(@Param("user") User user,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(g) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate AND g.readingValue > 250")
    long countCriticallyHighByUserAndDateRange(@Param("user") User user,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(g) FROM GlucoseReading g WHERE g.user = :user AND g.takenAt BETWEEN :startDate AND :endDate AND g.readingValue < 54")
    long countCriticallyLowByUserAndDateRange(@Param("user") User user,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
}