package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.RecipeRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRating, Long> {

    Optional<RecipeRating> findByUserIdAndRecipeId(UUID userId, Long recipeId);

    boolean existsByUserIdAndRecipeId(UUID userId, Long recipeId);

    @Query("SELECT AVG(r.score) FROM RecipeRating r WHERE r.recipe.id = :recipeId")
    Optional<Double> findAverageScoreByRecipeId(@Param("recipeId") Long recipeId);

    @Query("SELECT COUNT(r) FROM RecipeRating r WHERE r.recipe.id = :recipeId")
    long countByRecipeId(@Param("recipeId") Long recipeId);
}
