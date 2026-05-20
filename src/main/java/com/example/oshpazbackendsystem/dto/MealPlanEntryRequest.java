package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.MealType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Data
@NoArgsConstructor
public class MealPlanEntryRequest {

    @NotNull(message = "Retsept tanlanishi shart")
    private Long recipeId;

    @NotNull(message = "Hafta kuni tanlanishi shart")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Ovqat vaqti tanlanishi shart")
    private MealType mealType;

    @Min(value = 1)
    private Integer servings = 1;

    @Size(max = 255)
    private String notes;
}
