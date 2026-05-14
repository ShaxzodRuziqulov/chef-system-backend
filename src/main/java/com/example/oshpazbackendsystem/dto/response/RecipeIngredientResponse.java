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
public class RecipeIngredientResponse {

    private Long id;
    private Long ingredientId;
    private String ingredientNameUz;
    private String ingredientNameRu;
    private String ingredientNameEng;
    private Double amount;
    private MeasurementUnit unit;
    private String notes;
    private Integer orderIndex;
}
