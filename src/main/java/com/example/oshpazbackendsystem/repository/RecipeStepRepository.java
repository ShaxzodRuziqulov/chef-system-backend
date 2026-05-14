package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    // Retseptning bosqichlari tartib bo'yicha
    List<RecipeStep> findByRecipeIdOrderByStepNumberAsc(Long recipeId);

    // Retsept o'chirilganda bosqichlarini ham o'chirish
    @Modifying
    @Query("DELETE FROM RecipeStep s WHERE s.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}