package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Login uchun email bo'yicha topish
    Optional<User> findByEmail(String email);

    // Username bo'yicha topish
    Optional<User> findByUsername(String username);

    // Ro'yxatdan o'tishda email band emasligini tekshirish
    boolean existsByEmail(String email);

    // Ro'yxatdan o'tishda username band emasligini tekshirish
    boolean existsByUsername(String username);

    // Faqat faol foydalanuvchini email bo'yicha topish (login uchun)
    Optional<User> findByEmailAndActiveTrue(String email);

    // Role — single enum field, no JOIN needed; named explicitly for clarity
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findWithRolesByUsername(@Param("username") String username);

    // ── Admin: barcha foydalanuvchilar (qidiruv bilan) ───────────────────────

    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<User> searchByKeyword(@Param("q") String q, Pageable pageable);

    long countByActiveTrue();

    // ── Sevimlilar ───────────────────────────────────────────────────────────

    @Query(value = "SELECT recipe_id FROM user_favorites WHERE user_id = :userId", nativeQuery = true)
    Set<Long> findFavoriteIdsByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT COUNT(*) > 0 FROM user_favorites WHERE user_id = :userId AND recipe_id = :recipeId", nativeQuery = true)
    boolean isFavorited(@Param("userId") UUID userId, @Param("recipeId") Long recipeId);

    @Modifying
    @Query(value = "INSERT INTO user_favorites(user_id, recipe_id) VALUES (:userId, :recipeId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addFavorite(@Param("userId") UUID userId, @Param("recipeId") Long recipeId);

    @Modifying
    @Query(value = "DELETE FROM user_favorites WHERE user_id = :userId AND recipe_id = :recipeId", nativeQuery = true)
    void removeFavorite(@Param("userId") UUID userId, @Param("recipeId") Long recipeId);
}