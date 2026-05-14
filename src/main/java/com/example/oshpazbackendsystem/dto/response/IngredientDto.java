package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDto {

    private Long id;
    private String nameUz;
    private String nameRu;
    private String nameEng;
    private String description;
    private String imageUrl;
    private MeasurementUnit defaultUnit;
    private Double caloriesPer100g;
    private boolean allergen;
}
