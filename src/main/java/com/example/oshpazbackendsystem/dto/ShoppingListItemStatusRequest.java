package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.ShoppingItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemStatusRequest {

    @NotNull(message = "Status kiritilishi shart")
    private ShoppingItemStatus status;
}
