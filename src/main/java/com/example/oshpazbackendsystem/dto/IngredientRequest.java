package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequest {

    @NotBlank(message = "Ingredient nomi (UZ) bo'sh bo'lmasligi kerak")
    @Size(max = 100)
    private String nameUz;

    @Size(max = 100)
    private String nameRu;

    @Size(max = 100)
    private String nameEng;

    @Size(max = 500)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    private MeasurementUnit defaultUnit;

    private Double caloriesPer100g;

    private boolean allergen;
}
