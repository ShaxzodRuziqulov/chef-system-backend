package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientRequest {

    @NotNull(message = "Ingredient tanlanishi shart")
    private Long ingredientId;

    @NotNull(message = "Miqdor kiritilishi shart")
    @Min(value = 0, message = "Miqdor manfiy bo'lmasligi kerak")
    private Double amount;

    @NotNull(message = "O'lchov birligi tanlanishi shart")
    private MeasurementUnit unit;

    @Size(max = 255)
    private String notes;

    private Integer orderIndex;
}
