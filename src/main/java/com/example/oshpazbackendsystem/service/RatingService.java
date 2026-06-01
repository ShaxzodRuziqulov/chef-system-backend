package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.RatingRequest;
import com.example.oshpazbackendsystem.dto.response.RatingResultDto;
import com.example.oshpazbackendsystem.entity.Recipe;
import com.example.oshpazbackendsystem.entity.RecipeRating;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.exception.NotFoundException;
import com.example.oshpazbackendsystem.repository.RecipeRatingRepository;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RecipeRatingRepository ratingRepository;
    private final RecipeRepository       recipeRepository;
    private final CurrentUserService     currentUserService;

    public RatingResultDto rate(Long recipeId, RatingRequest request) {
        User   user   = currentUserService.getCurrentUser();
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + recipeId));

        RecipeRating rating = ratingRepository
                .findByUserIdAndRecipeId(user.getId(), recipeId)
                .orElseGet(() -> RecipeRating.builder().user(user).recipe(recipe).build());

        rating.setScore(request.getScore().shortValue());
        ratingRepository.save(rating);

        // Recipe jadvalidagi keshni yangilash
        updateRecipeRatingCache(recipe, recipeId);

        return buildResult(user.getId(), recipeId);
    }

    @Transactional(readOnly = true)
    public RatingResultDto getMyRating(Long recipeId) {
        UUID userId = currentUserService.getCurrentUserIdOrNull();
        return buildResult(userId, recipeId);
    }

    // ── Yordamchi metodlar ────────────────────────────────────────────────────

    private void updateRecipeRatingCache(Recipe recipe, Long recipeId) {
        double avg   = ratingRepository.findAverageScoreByRecipeId(recipeId).orElse(0.0);
        long   count = ratingRepository.countByRecipeId(recipeId);
        // Round to 1 decimal
        recipe.setAverageRating(Math.round(avg * 10.0) / 10.0);
        recipe.setRatingCount((int) count);
        recipeRepository.save(recipe);
    }

    private RatingResultDto buildResult(UUID userId, Long recipeId) {
        int    myScore = 0;
        if (userId != null) {
            myScore = ratingRepository.findByUserIdAndRecipeId(userId, recipeId)
                    .map(r -> (int) r.getScore())
                    .orElse(0);
        }
        double avg   = ratingRepository.findAverageScoreByRecipeId(recipeId).orElse(0.0);
        long   count = ratingRepository.countByRecipeId(recipeId);

        return RatingResultDto.builder()
                .myScore(myScore)
                .averageRating(Math.round(avg * 10.0) / 10.0)
                .ratingCount(count)
                .build();
    }
}
