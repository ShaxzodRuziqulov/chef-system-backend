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
public class RecipeDto {

    private Long id;
    private String titleUz;
    private String titleRu;
    private String titleEng;
    private String description;

    // Kategoriya
    private Long categoryId;
    private String categoryNameUz;
    private String categoryNameRu;
    private String categoryNameEng;

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
    private Set<TagDto> tags;

    // Tafsilotlar
    private List<RecipeIngredientDto> ingredients;
    private List<RecipeStepDto> steps;
    private List<RecipeImageDto> images;
    private NutritionalInfoDto nutritionalInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
