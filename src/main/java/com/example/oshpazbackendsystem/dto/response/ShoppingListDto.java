package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListDto {

    private Long id;

    // Foydalanuvchi
    private UUID userId;

    // Bog'liq haftalik reja (ixtiyoriy)
    private Long mealPlanId;
    private String mealPlanName;

    // Ro'yxat oxirgi marta qayta yaratilgan vaqt
    private LocalDateTime generatedAt;

    // Ro'yxat
    private String name;
    private boolean completed;

    // Mahsulotlar
    private List<ShoppingListItemDto> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
