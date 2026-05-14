package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.ShoppingListItemResponse;
import com.example.oshpazbackendsystem.dto.response.ShoppingListResponse;
import com.example.oshpazbackendsystem.entity.ShoppingList;
import com.example.oshpazbackendsystem.entity.ShoppingListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShoppingListMapper {

    // ─── Entity → ShoppingListResponse ───────────────────────────────────────
    @Mapping(target = "userId",       source = "user.id")
    @Mapping(target = "mealPlanId",   source = "mealPlan.id")
    @Mapping(target = "mealPlanName", source = "mealPlan.name")
    ShoppingListResponse toResponse(ShoppingList shoppingList);

    // ─── ShoppingListItem → ShoppingListItemResponse ──────────────────────────
    @Mapping(target = "ingredientId",      source = "ingredient.id")
    @Mapping(target = "ingredientNameUz",  source = "ingredient.nameUz")
    @Mapping(target = "ingredientNameRu",  source = "ingredient.nameRu")
    @Mapping(target = "ingredientNameEng", source = "ingredient.nameEng")
    ShoppingListItemResponse toItemResponse(ShoppingListItem item);
}
