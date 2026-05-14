package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // Uzbekcha nomi bo'yicha topish
    Optional<Ingredient> findByNameUzIgnoreCase(String nameUz);

    // Mavjudligini tekshirish
    boolean existsByNameUz(String nameUz);

    // Retseptga ingredient qo'shishda ko'p tilli qidiruv
    @Query("""
            SELECT i FROM Ingredient i
            WHERE LOWER(i.nameUz)  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(i.nameRu)  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(i.nameEng) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Ingredient> searchByName(@Param("keyword") String keyword, Pageable pageable);
}