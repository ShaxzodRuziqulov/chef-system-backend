package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.NutritionalInfoRequest;
import com.example.oshpazbackendsystem.dto.RecipeCreateRequest;
import com.example.oshpazbackendsystem.dto.RecipeIngredientRequest;
import com.example.oshpazbackendsystem.dto.RecipeStepRequest;
import com.example.oshpazbackendsystem.dto.RecipeUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.NutritionalInfoResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeImageResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeIngredientResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeStepResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeSummaryResponse;
import com.example.oshpazbackendsystem.entity.NutritionalInfo;
import com.example.oshpazbackendsystem.entity.Recipe;
import com.example.oshpazbackendsystem.entity.RecipeImage;
import com.example.oshpazbackendsystem.entity.RecipeIngredient;
import com.example.oshpazbackendsystem.entity.RecipeStep;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecipeMapper {

    // ─── Entity → RecipeSummaryResponse (ro'yxat uchun) ──────────────────────
    @Mapping(target = "categoryId",       source = "category.id")
    @Mapping(target = "categoryNameUz",   source = "category.nameUz")
    @Mapping(target = "authorId",         source = "author.id")
    @Mapping(target = "authorFullName",   source = "author.fullName")
    @Mapping(target = "totalTimeMinutes",
             expression = "java(recipe.getPrepTimeMinutes() + recipe.getCookTimeMinutes())")
    RecipeSummaryResponse toSummaryResponse(Recipe recipe);

    // ─── Entity → RecipeResponse (to'liq sahifa uchun) ───────────────────────
    @Mapping(target = "categoryId",       source = "category.id")
    @Mapping(target = "categoryNameUz",   source = "category.nameUz")
    @Mapping(target = "authorId",         source = "author.id")
    @Mapping(target = "authorFullName",   source = "author.fullName")
    @Mapping(target = "totalTimeMinutes",
             expression = "java(recipe.getPrepTimeMinutes() + recipe.getCookTimeMinutes())")
    RecipeResponse toResponse(Recipe recipe);

    // ─── RecipeCreateRequest → Entity ────────────────────────────────────────
    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "author",          ignore = true)
    @Mapping(target = "category",        ignore = true)
    @Mapping(target = "tags",            ignore = true)
    @Mapping(target = "ingredients",     ignore = true)
    @Mapping(target = "steps",           ignore = true)
    @Mapping(target = "images",          ignore = true)
    @Mapping(target = "nutritionalInfo", ignore = true)
    @Mapping(target = "deleted",         ignore = true)
    @Mapping(target = "viewCount",       ignore = true)
    @Mapping(target = "averageRating",   ignore = true)
    @Mapping(target = "ratingCount",     ignore = true)
    @Mapping(target = "version",         ignore = true)
    Recipe toEntity(RecipeCreateRequest request);

    // ─── RecipeUpdateRequest → mavjud Entity ni yangilash (PATCH) ────────────
    // null fieldlar o'tkazib yuboriladi — faqat to'ldirilganlar yangilanadi
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "author",          ignore = true)
    @Mapping(target = "category",        ignore = true)
    @Mapping(target = "tags",            ignore = true)
    @Mapping(target = "ingredients",     ignore = true)
    @Mapping(target = "steps",           ignore = true)
    @Mapping(target = "images",          ignore = true)
    @Mapping(target = "nutritionalInfo", ignore = true)
    @Mapping(target = "deleted",         ignore = true)
    @Mapping(target = "viewCount",       ignore = true)
    @Mapping(target = "averageRating",   ignore = true)
    @Mapping(target = "ratingCount",     ignore = true)
    @Mapping(target = "version",         ignore = true)
    void updateEntity(RecipeUpdateRequest request, @MappingTarget Recipe recipe);

    // ─── RecipeIngredientRequest → Entity ─────────────────────────────────────
    // ingredient va recipe service da set qilinadi
    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "recipe",     ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    RecipeIngredient toIngredientEntity(RecipeIngredientRequest request);

    // ─── RecipeStepRequest → Entity ───────────────────────────────────────────
    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "recipe", ignore = true)
    RecipeStep toStepEntity(RecipeStepRequest request);

    // ─── NutritionalInfoRequest → Entity ──────────────────────────────────────
    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "recipe", ignore = true)
    NutritionalInfo toNutritionalInfoEntity(NutritionalInfoRequest request);

    // ─── NutritionalInfo → NutritionalInfoResponse ────────────────────────────
    NutritionalInfoResponse toNutritionalInfoResponse(NutritionalInfo nutritionalInfo);

    // ─── RecipeIngredient → RecipeIngredientResponse ──────────────────────────
    @Mapping(target = "ingredientId",      source = "ingredient.id")
    @Mapping(target = "ingredientNameUz",  source = "ingredient.nameUz")
    @Mapping(target = "ingredientNameRu",  source = "ingredient.nameRu")
    @Mapping(target = "ingredientNameEng", source = "ingredient.nameEng")
    RecipeIngredientResponse toIngredientResponse(RecipeIngredient recipeIngredient);

    // ─── RecipeStep → RecipeStepResponse ──────────────────────────────────────
    RecipeStepResponse toStepResponse(RecipeStep step);

    // ─── RecipeImage → RecipeImageResponse ────────────────────────────────────
    RecipeImageResponse toImageResponse(RecipeImage image);
}
