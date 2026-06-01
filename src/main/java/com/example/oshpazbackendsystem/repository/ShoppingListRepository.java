package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.ShoppingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    // Foydalanuvchining barcha savdo ro'yxatlari
    Page<ShoppingList> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // Haftalik reja asosida yaratilgan savdo ro'yxati (itemlar bilan birga)
    @Query("""
            SELECT sl FROM ShoppingList sl
            LEFT JOIN FETCH sl.items i
            LEFT JOIN FETCH i.ingredient
            WHERE sl.mealPlan.id = :mealPlanId
            """)
    Optional<ShoppingList> findByMealPlanId(@Param("mealPlanId") Long mealPlanId);

    // Foydalanuvchining tugallanmagan ro'yxatlari
    Page<ShoppingList> findByUserIdAndCompletedFalse(UUID userId, Pageable pageable);

    // Ro'yxatni elementlari bilan birga yuklash
    @Query("""
            SELECT sl FROM ShoppingList sl
            LEFT JOIN FETCH sl.items i
            LEFT JOIN FETCH i.ingredient
            WHERE sl.id = :id
            """)
    Optional<ShoppingList> findByIdWithItems(@Param("id") Long id);
}