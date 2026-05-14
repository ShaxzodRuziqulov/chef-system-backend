package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.MealPlanCreateRequest;
import com.example.oshpazbackendsystem.dto.MealPlanEntryRequest;
import com.example.oshpazbackendsystem.dto.response.MealPlanEntryResponse;
import com.example.oshpazbackendsystem.dto.response.MealPlanResponse;
import com.example.oshpazbackendsystem.dto.response.MealPlanSummaryResponse;
import com.example.oshpazbackendsystem.entity.MealPlan;
import com.example.oshpazbackendsystem.entity.MealPlanEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealPlanMapper {

    // ─── MealPlanCreateRequest → Entity ──────────────────────────────────────
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "user",         ignore = true)
    @Mapping(target = "entries",      ignore = true)
    @Mapping(target = "shoppingList", ignore = true)
    @Mapping(target = "status",       ignore = true)
    @Mapping(target = "version",      ignore = true)
    // weekEndDate = weekStartDate + 6 kun (Dush-Yaks)
    @Mapping(target = "weekEndDate",
             expression = "java(request.getWeekStartDate().plusDays(6))")
    MealPlan toEntity(MealPlanCreateRequest request);

    // ─── Entity → MealPlanResponse (to'liq, entriylar bilan) ─────────────────
    @Mapping(target = "userId",       source = "user.id")
    @Mapping(target = "userFullName", source = "user.fullName")
    MealPlanResponse toResponse(MealPlan mealPlan);

    // ─── Entity → MealPlanSummaryResponse (ro'yxat uchun) ────────────────────
    @Mapping(target = "entryCount",
             expression = "java(mealPlan.getEntries() != null ? mealPlan.getEntries().size() : 0)")
    MealPlanSummaryResponse toSummaryResponse(MealPlan mealPlan);

    // ─── MealPlanEntryRequest → Entity ───────────────────────────────────────
    // mealPlan va recipe service da set qilinadi
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "mealPlan", ignore = true)
    @Mapping(target = "recipe",   ignore = true)
    MealPlanEntry toEntryEntity(MealPlanEntryRequest request);

    // ─── MealPlanEntry → MealPlanEntryResponse ────────────────────────────────
    @Mapping(target = "recipeId",       source = "recipe.id")
    @Mapping(target = "recipeTitleUz",  source = "recipe.titleUz")
    @Mapping(target = "recipeTitleRu",  source = "recipe.titleRu")
    @Mapping(target = "recipeImageUrl", source = "recipe.imageUrl")
    MealPlanEntryResponse toEntryResponse(MealPlanEntry entry);
}
