package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    // Retseptning barcha ingredientlari
    List<RecipeIngredient> findByRecipeIdOrderByOrderIndexAsc(Long recipeId);

    // Retsept o'chirilganda ingredientlarini ham o'chirish
    @Modifying
    @Query("DELETE FROM RecipeIngredient ri WHERE ri.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}