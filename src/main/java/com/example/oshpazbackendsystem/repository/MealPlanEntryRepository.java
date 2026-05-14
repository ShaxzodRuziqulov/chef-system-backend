package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.MealPlanEntry;
import com.example.oshpazbackendsystem.entity.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanEntryRepository extends JpaRepository<MealPlanEntry, Long> {

    // Rejaning barcha elementlari
    List<MealPlanEntry> findByMealPlanId(Long mealPlanId);

    // Muayyan kun elementlari (masalan, Dushanba)
    List<MealPlanEntry> findByMealPlanIdAndDayOfWeek(Long mealPlanId, DayOfWeek dayOfWeek);

    // Muayyan kun va ovqat vaqti (Dushanba + LUNCH)
    Optional<MealPlanEntry> findByMealPlanIdAndDayOfWeekAndMealType(
            Long mealPlanId, DayOfWeek dayOfWeek, MealType mealType);

    // Reja o'chirilganda barcha elementlarini o'chirish
    @Modifying
    @Query("DELETE FROM MealPlanEntry e WHERE e.mealPlan.id = :mealPlanId")
    void deleteByMealPlanId(@Param("mealPlanId") Long mealPlanId);
}