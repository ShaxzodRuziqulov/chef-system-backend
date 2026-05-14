package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.Recipe;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
        JpaSpecificationExecutor<Recipe> {

    // Hamma ochiq retseptlar — bosh sahifa uchun
    Page<Recipe> findByDeletedFalseAndVisibleTrue(Pageable pageable);

    // Muallif bo'yicha retseptlar — "Mening retseptlarim"
    Page<Recipe> findByAuthorIdAndDeletedFalse(UUID authorId, Pageable pageable);

    // Kategoriya bo'yicha retseptlar
    Page<Recipe> findByCategoryIdAndDeletedFalseAndVisibleTrue(Long categoryId, Pageable pageable);

    // Qiyinlik bo'yicha filtr
    Page<Recipe> findByDifficultyLevelAndDeletedFalseAndVisibleTrue(
            DifficultyLevel difficultyLevel, Pageable pageable);

    // Ko'p tilli qidiruv — uz, ru, eng bir vaqtda
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.deleted = false
              AND r.visible = true
              AND (
                LOWER(r.titleUz)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(r.titleRu)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(r.titleEng) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Recipe> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    // Retseptni ingredientlari bilan birga yuklash (N+1 muammosini oldini olish)
    @Query("""
            SELECT DISTINCT r FROM Recipe r
            LEFT JOIN FETCH r.ingredients ri
            LEFT JOIN FETCH ri.ingredient
            WHERE r.id = :id AND r.deleted = false
            """)
    Optional<Recipe> findByIdWithIngredients(@Param("id") Long id);

    // Retseptni barcha ma'lumotlari bilan yuklash — to'liq sahifa uchun
    @Query("""
            SELECT DISTINCT r FROM Recipe r
            LEFT JOIN FETCH r.ingredients ri
            LEFT JOIN FETCH ri.ingredient
            LEFT JOIN FETCH r.steps
            LEFT JOIN FETCH r.images
            LEFT JOIN FETCH r.tags
            WHERE r.id = :id AND r.deleted = false
            """)
    Optional<Recipe> findByIdWithDetails(@Param("id") Long id);

    // Ko'rishlar sonini oshirish
    @Modifying
    @Query("UPDATE Recipe r SET r.viewCount = r.viewCount + 1 WHERE r.id = :id")
    void incrementViewCount(@Param("id") Long id);
}