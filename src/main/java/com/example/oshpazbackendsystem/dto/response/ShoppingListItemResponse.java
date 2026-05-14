package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import com.example.oshpazbackendsystem.entity.enums.ShoppingItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemResponse {

    private Long id;

    // Ingredient
    private Long ingredientId;
    private String ingredientNameUz;
    private String ingredientNameRu;
    private String ingredientNameEng;

    // Miqdor
    private Double amount;
    private MeasurementUnit unit;

    // Holat
    private ShoppingItemStatus status;

    // Qo'shimcha
    private BigDecimal estimatedPrice;
    private String notes;
    private String grocerySection;  // Qaysi bo'lim: Sabzavotlar, Sut, Go'sht...
}
