package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {

    private Long id;
    private String titleUz;
    private String titleRu;
    private String titleEng;
    private String description;

    // Kategoriya
    private Long categoryId;
    private String categoryNameUz;

    // Vaqt va porsiya
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer totalTimeMinutes;    // prep + cook
    private Integer servings;
    private DifficultyLevel difficultyLevel;

    // Media
    private String imageUrl;
    private String videoUrl;

    // Holat
    private boolean visible;

    // Statistika
    private Double averageRating;
    private Integer ratingCount;
    private Long viewCount;

    // Muallif
    private UUID authorId;
    private String authorFullName;

    // Teglar
    private Set<TagResponse> tags;

    // Tafsilotlar
    private List<RecipeIngredientResponse> ingredients;
    private List<RecipeStepResponse> steps;
    private List<RecipeImageResponse> images;
    private NutritionalInfoResponse nutritionalInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
