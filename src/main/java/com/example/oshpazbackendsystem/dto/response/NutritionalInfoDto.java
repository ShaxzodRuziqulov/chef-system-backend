package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalInfoDto {

    private Long id;
    private Double caloriesPerServing;
    private Double proteinGrams;
    private Double fatGrams;
    private Double carbohydrateGrams;
    private Double fiberGrams;
    private Double sugarGrams;
    private Double sodiumMg;
}
