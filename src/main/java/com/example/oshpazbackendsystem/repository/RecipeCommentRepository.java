package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.RecipeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {

    @Query("""
           SELECT c FROM RecipeComment c
           JOIN FETCH c.user
           WHERE c.recipe.id = :recipeId AND c.deleted = false
           ORDER BY c.createdAt DESC
           """)
    Page<RecipeComment> findActiveByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    long countByRecipeIdAndDeletedFalse(Long recipeId);
}
