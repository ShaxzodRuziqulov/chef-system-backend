package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeImageRepository extends JpaRepository<RecipeImage, Long> {

    // Retseptning barcha rasmlari tartib bo'yicha
    List<RecipeImage> findByRecipeIdOrderByOrderIndexAsc(Long recipeId);

    // Retseptning asosiy rasmi
    Optional<RecipeImage> findByRecipeIdAndPrimaryImageTrue(Long recipeId);

    // Retsept o'chirilganda rasmlarini ham o'chirish
    @Modifying
    @Query("DELETE FROM RecipeImage i WHERE i.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}
