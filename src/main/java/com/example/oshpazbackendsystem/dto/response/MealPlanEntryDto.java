package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanEntryDto {

    private Long id;

    // Retsept
    private Long recipeId;
    private String recipeTitleUz;
    private String recipeTitleRu;
    private String recipeTitleEng;
    private String recipeImageUrl;

    // Reja
    private DayOfWeek dayOfWeek;
    private MealType mealType;
    private Integer servings;
    private String notes;
}
