package com.example.oshpazbackendsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalInfoRequest {

    private Double caloriesPerServing;
    private Double proteinGrams;
    private Double fatGrams;
    private Double carbohydrateGrams;
    private Double fiberGrams;
    private Double sugarGrams;
    private Double sodiumMg;
}
