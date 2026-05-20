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
import java.util.Set;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
        JpaSpecificationExecutor<Recipe> {

    // Bosh sahifa: public retseptlar + joriy foydalanuvchining shaxsiy retseptlari
    // viewerId null bo'lsa (anonim) faqat visible=true qaytadi
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.deleted = false
              AND (r.visible = true OR r.author.id = :viewerId)
            """)
    Page<Recipe> findVisibleOrOwned(@Param("viewerId") UUID viewerId, Pageable pageable);

    // Muallif bo'yicha retseptlar — "Mening retseptlarim"
    Page<Recipe> findByAuthorIdAndDeletedFalse(UUID authorId, Pageable pageable);

    // Kategoriya bo'yicha: public + joriy foydalanuvchi shaxsiylari
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.deleted = false
              AND r.category.id = :categoryId
              AND (r.visible = true OR r.author.id = :viewerId)
            """)
    Page<Recipe> findByCategoryVisibleOrOwned(
            @Param("categoryId") Long categoryId,
            @Param("viewerId") UUID viewerId,
            Pageable pageable);

    // Qiyinlik bo'yicha: public + joriy foydalanuvchi shaxsiylari
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.deleted = false
              AND r.difficultyLevel = :level
              AND (r.visible = true OR r.author.id = :viewerId)
            """)
    Page<Recipe> findByDifficultyVisibleOrOwned(
            @Param("level") DifficultyLevel level,
            @Param("viewerId") UUID viewerId,
            Pageable pageable);

    // Ko'p tilli qidiruv — uz, ru, eng (public + joriy foydalanuvchi shaxsiylari)
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.deleted = false
              AND (r.visible = true OR r.author.id = :viewerId)
              AND (
                LOWER(r.titleUz)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(r.titleRu)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(r.titleEng) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Recipe> searchByTitle(
            @Param("keyword") String keyword,
            @Param("viewerId") UUID viewerId,
            Pageable pageable);

    // Retseptni ingredientlari bilan birga yuklash (N+1 muammosini oldini olish)
    @Query("""
            SELECT DISTINCT r FROM Recipe r
            LEFT JOIN FETCH r.ingredients ri
            LEFT JOIN FETCH ri.ingredient
            WHERE r.id = :id AND r.deleted = false
            """)
    Optional<Recipe> findByIdWithIngredients(@Param("id") Long id);

    // Retseptni ingredientlari + teglari bilan yuklash
    // DIQQAT: Hibernate bir so'rovda faqat BITTA List (bag) ni JOIN FETCH qila oladi.
    // steps va images ni RecipeService ichida Hibernate.initialize() bilan yuklaymiz.
    @Query("""
            SELECT DISTINCT r FROM Recipe r
            LEFT JOIN FETCH r.ingredients ri
            LEFT JOIN FETCH ri.ingredient
            LEFT JOIN FETCH r.tags
            WHERE r.id = :id AND r.deleted = false
            """)
    Optional<Recipe> findByIdWithDetails(@Param("id") Long id);

    // Ko'rishlar sonini oshirish
    @Modifying
    @Query("UPDATE Recipe r SET r.viewCount = r.viewCount + 1 WHERE r.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Sevimlilar: foydalanuvchi saqlagan retseptlar (sahifalangan)
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.deleted = false
              AND r.id IN (
                SELECT rf.id FROM User u JOIN u.favorites rf WHERE u.id = :userId
              )
            ORDER BY r.createdAt DESC
            """)
    Page<Recipe> findFavoritesByUserId(@Param("userId") UUID userId, Pageable pageable);
}