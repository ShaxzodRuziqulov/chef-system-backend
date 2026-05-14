package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateRequest {

    @NotBlank(message = "Retsept nomi (UZ) bo'sh bo'lmasligi kerak")
    @Size(max = 200)
    private String titleUz;

    @Size(max = 200)
    private String titleRu;

    @Size(max = 200)
    private String titleEng;

    private String description;

    private Long categoryId;

    private Set<Long> tagIds;

    @NotNull(message = "Tayyorgarlik vaqti kiritilishi shart")
    @Min(value = 1)
    private Integer prepTimeMinutes;

    @NotNull(message = "Pishirish vaqti kiritilishi shart")
    @Min(value = 0)
    private Integer cookTimeMinutes;

    @NotNull(message = "Porsiya soni kiritilishi shart")
    @Min(value = 1)
    private Integer servings;

    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 500)
    private String videoUrl;

    private boolean visible = true;

    @Valid
    @NotEmpty(message = "Kamida bitta ingredient bo'lishi kerak")
    private List<RecipeIngredientRequest> ingredients;

    @Valid
    @NotEmpty(message = "Kamida bitta bosqich bo'lishi kerak")
    private List<RecipeStepRequest> steps;

    @Valid
    private NutritionalInfoRequest nutritionalInfo;
}
