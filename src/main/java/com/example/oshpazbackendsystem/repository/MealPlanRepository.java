package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.MealPlan;
import com.example.oshpazbackendsystem.entity.enums.PlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    // Foydalanuvchining barcha rejalari
    Page<MealPlan> findByUserIdOrderByWeekStartDateDesc(UUID userId, Pageable pageable);

    // Foydalanuvchining faol rejalari
    Page<MealPlan> findByUserIdAndStatus(UUID userId, PlanStatus status, Pageable pageable);

    // Muayyan hafta rejasini topish
    Optional<MealPlan> findByUserIdAndWeekStartDate(UUID userId, LocalDate weekStartDate);

    // Rejaningsentrilarini ham birga yuklash (N+1 oldini olish)
    @Query("""
            SELECT DISTINCT mp FROM MealPlan mp
            LEFT JOIN FETCH mp.entries e
            LEFT JOIN FETCH e.recipe
            WHERE mp.id = :id
            """)
    Optional<MealPlan> findByIdWithEntries(@Param("id") Long id);
}