package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class RecipeUpdateRequest {

    @Size(max = 200)
    private String titleUz;

    @Size(max = 200)
    private String titleRu;

    @Size(max = 200)
    private String titleEng;

    private String description;

    private Long categoryId;

    private Set<Long> tagIds;

    @Min(value = 1)
    private Integer prepTimeMinutes;

    @Min(value = 0)
    private Integer cookTimeMinutes;

    @Min(value = 1)
    private Integer servings;

    private DifficultyLevel difficultyLevel;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 500)
    private String videoUrl;

    private Boolean visible;

    @Valid
    private List<RecipeIngredientRequest> ingredients;

    @Valid
    private List<RecipeStepRequest> steps;

    @Valid
    private NutritionalInfoRequest nutritionalInfo;
}
