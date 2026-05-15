package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.RecipeCreateRequest;
import com.example.oshpazbackendsystem.dto.RecipeUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.*;
import com.example.oshpazbackendsystem.entity.*;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.repository.*;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final IngredientRepository ingredientRepository;
    private final CurrentUserService currentUserService;

    // ── O'qish ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<RecipeDto> findAll(Pageable pageable) {
        return recipeRepository.findByDeletedFalseAndVisibleTrue(pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RecipeDto findById(Long id) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + id));
        return toDto(recipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeDto> search(String keyword, Pageable pageable) {
        return recipeRepository.searchByTitle(keyword, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecipeDto> findByCategory(Long categoryId, Pageable pageable) {
        return recipeRepository.findByCategoryIdAndDeletedFalseAndVisibleTrue(categoryId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecipeDto> findByDifficulty(DifficultyLevel level, Pageable pageable) {
        return recipeRepository.findByDifficultyLevelAndDeletedFalseAndVisibleTrue(level, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecipeDto> findMyRecipes(Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();
        return recipeRepository.findByAuthorIdAndDeletedFalse(currentUser.getId(), pageable)
                .map(this::toDto);
    }

    // ── Yozish ───────────────────────────────────────────────────────────────

    public RecipeDto create(RecipeCreateRequest request) {
        User author = currentUserService.getCurrentUser();

        Recipe recipe = Recipe.builder()
                .titleUz(request.getTitleUz())
                .titleRu(request.getTitleRu())
                .titleEng(request.getTitleEng())
                .description(request.getDescription())
                .prepTimeMinutes(request.getPrepTimeMinutes())
                .cookTimeMinutes(request.getCookTimeMinutes())
                .servings(request.getServings())
                .difficultyLevel(request.getDifficultyLevel() != null
                        ? request.getDifficultyLevel() : DifficultyLevel.MEDIUM)
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .visible(request.isVisible())
                .author(author)
                .build();

        // Kategoriya
        if (request.getCategoryId() != null) {
            recipe.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(
                            "CATEGORY_NOT_FOUND", "Kategoriya topilmadi: " + request.getCategoryId())));
        }

        // Teglar
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            recipe.setTags(tags);
        }

        // Avval recipeни saqlash (ID hosil bo'ladi)
        recipeRepository.save(recipe);

        // Ingredientlar
        if (request.getIngredients() != null) {
            List<RecipeIngredient> ingredients = new ArrayList<>();
            for (var req : request.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(req.getIngredientId())
                        .orElseThrow(() -> new NotFoundException(
                                "INGREDIENT_NOT_FOUND", "Ingredient topilmadi: " + req.getIngredientId()));
                ingredients.add(RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .amount(req.getAmount())
                        .unit(req.getUnit())
                        .notes(req.getNotes())
                        .orderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 0)
                        .build());
            }
            recipe.setIngredients(ingredients);
        }

        // Bosqichlar
        if (request.getSteps() != null) {
            List<RecipeStep> steps = new ArrayList<>();
            for (var req : request.getSteps()) {
                steps.add(RecipeStep.builder()
                        .recipe(recipe)
                        .stepNumber(req.getStepNumber())
                        .instruction(req.getInstruction())
                        .imageUrl(req.getImageUrl())
                        .durationMinutes(req.getDurationMinutes())
                        .build());
            }
            recipe.setSteps(steps);
        }

        // Ozuqaviy ma'lumot
        if (request.getNutritionalInfo() != null) {
            var ni = request.getNutritionalInfo();
            recipe.setNutritionalInfo(NutritionalInfo.builder()
                    .recipe(recipe)
                    .caloriesPerServing(ni.getCaloriesPerServing())
                    .proteinGrams(ni.getProteinGrams())
                    .fatGrams(ni.getFatGrams())
                    .carbohydrateGrams(ni.getCarbohydrateGrams())
                    .fiberGrams(ni.getFiberGrams())
                    .sugarGrams(ni.getSugarGrams())
                    .sodiumMg(ni.getSodiumMg())
                    .build());
        }

        return toDto(recipeRepository.save(recipe));
    }

    public RecipeDto update(Long id, RecipeUpdateRequest request) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + id));

        checkOwnership(recipe);

        if (request.getTitleUz() != null)       recipe.setTitleUz(request.getTitleUz());
        if (request.getTitleRu() != null)        recipe.setTitleRu(request.getTitleRu());
        if (request.getTitleEng() != null)       recipe.setTitleEng(request.getTitleEng());
        if (request.getDescription() != null)    recipe.setDescription(request.getDescription());
        if (request.getPrepTimeMinutes() != null) recipe.setPrepTimeMinutes(request.getPrepTimeMinutes());
        if (request.getCookTimeMinutes() != null) recipe.setCookTimeMinutes(request.getCookTimeMinutes());
        if (request.getServings() != null)       recipe.setServings(request.getServings());
        if (request.getDifficultyLevel() != null) recipe.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getImageUrl() != null)       recipe.setImageUrl(request.getImageUrl());
        if (request.getVideoUrl() != null)       recipe.setVideoUrl(request.getVideoUrl());
        if (request.getVisible() != null)        recipe.setVisible(request.getVisible());

        if (request.getCategoryId() != null) {
            recipe.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(
                            "CATEGORY_NOT_FOUND", "Kategoriya topilmadi: " + request.getCategoryId())));
        }

        if (request.getTagIds() != null) {
            recipe.setTags(new HashSet<>(tagRepository.findAllById(request.getTagIds())));
        }

        if (request.getIngredients() != null) {
            recipe.getIngredients().clear();
            for (var req : request.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(req.getIngredientId())
                        .orElseThrow(() -> new NotFoundException(
                                "INGREDIENT_NOT_FOUND", "Ingredient topilmadi: " + req.getIngredientId()));
                recipe.getIngredients().add(RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .amount(req.getAmount())
                        .unit(req.getUnit())
                        .notes(req.getNotes())
                        .orderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 0)
                        .build());
            }
        }

        if (request.getSteps() != null) {
            recipe.getSteps().clear();
            for (var req : request.getSteps()) {
                recipe.getSteps().add(RecipeStep.builder()
                        .recipe(recipe)
                        .stepNumber(req.getStepNumber())
                        .instruction(req.getInstruction())
                        .imageUrl(req.getImageUrl())
                        .durationMinutes(req.getDurationMinutes())
                        .build());
            }
        }

        if (request.getNutritionalInfo() != null) {
            var ni = request.getNutritionalInfo();
            if (recipe.getNutritionalInfo() == null) {
                recipe.setNutritionalInfo(NutritionalInfo.builder().recipe(recipe).build());
            }
            recipe.getNutritionalInfo().setCaloriesPerServing(ni.getCaloriesPerServing());
            recipe.getNutritionalInfo().setProteinGrams(ni.getProteinGrams());
            recipe.getNutritionalInfo().setFatGrams(ni.getFatGrams());
            recipe.getNutritionalInfo().setCarbohydrateGrams(ni.getCarbohydrateGrams());
            recipe.getNutritionalInfo().setFiberGrams(ni.getFiberGrams());
            recipe.getNutritionalInfo().setSugarGrams(ni.getSugarGrams());
            recipe.getNutritionalInfo().setSodiumMg(ni.getSodiumMg());
        }

        return toDto(recipeRepository.save(recipe));
    }

    public void delete(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + id));
        checkOwnership(recipe);
        recipe.setDeleted(true);   // Soft delete
        recipeRepository.save(recipe);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        recipeRepository.incrementViewCount(id);
    }

    // ── Ichki metodlar ───────────────────────────────────────────────────────

    private void checkOwnership(Recipe recipe) {
        User current = currentUserService.getCurrentUser();
        boolean isAdmin = current.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !recipe.getAuthor().getId().equals(current.getId())) {
            throw new IllegalStateException("Bu retseptni o'zgartirish uchun ruxsat yo'q");
        }
    }

    // ── Manual mapping: Recipe → RecipeDto ───────────────────────────────────

    public RecipeDto toDto(Recipe r) {
        return RecipeDto.builder()
                .id(r.getId())
                .titleUz(r.getTitleUz())
                .titleRu(r.getTitleRu())
                .titleEng(r.getTitleEng())
                .description(r.getDescription())
                .categoryId(r.getCategory() != null ? r.getCategory().getId() : null)
                .categoryNameUz(r.getCategory() != null ? r.getCategory().getNameUz() : null)
                .prepTimeMinutes(r.getPrepTimeMinutes())
                .cookTimeMinutes(r.getCookTimeMinutes())
                .totalTimeMinutes(r.getPrepTimeMinutes() + r.getCookTimeMinutes())
                .servings(r.getServings())
                .difficultyLevel(r.getDifficultyLevel())
                .imageUrl(r.getImageUrl())
                .videoUrl(r.getVideoUrl())
                .visible(r.isVisible())
                .averageRating(r.getAverageRating())
                .ratingCount(r.getRatingCount())
                .viewCount(r.getViewCount())
                .authorId(r.getAuthor() != null ? r.getAuthor().getId() : null)
                .authorFullName(r.getAuthor() != null ? r.getAuthor().getFullName() : null)
                .tags(r.getTags() != null
                        ? r.getTags().stream().map(t -> TagDto.builder()
                                .id(t.getId())
                                .nameUz(t.getNameUz())
                                .nameRu(t.getNameRu())
                                .nameEng(t.getNameEng())
                                .build()).collect(Collectors.toSet())
                        : Set.of())
                .ingredients(r.getIngredients() != null
                        ? r.getIngredients().stream().map(ri -> RecipeIngredientDto.builder()
                                .id(ri.getId())
                                .ingredientId(ri.getIngredient().getId())
                                .ingredientNameUz(ri.getIngredient().getNameUz())
                                .ingredientNameRu(ri.getIngredient().getNameRu())
                                .ingredientNameEng(ri.getIngredient().getNameEng())
                                .amount(ri.getAmount())
                                .unit(ri.getUnit())
                                .notes(ri.getNotes())
                                .orderIndex(ri.getOrderIndex())
                                .build()).collect(Collectors.toList())
                        : List.of())
                .steps(r.getSteps() != null
                        ? r.getSteps().stream().map(s -> RecipeStepDto.builder()
                                .id(s.getId())
                                .stepNumber(s.getStepNumber())
                                .instruction(s.getInstruction())
                                .imageUrl(s.getImageUrl())
                                .durationMinutes(s.getDurationMinutes())
                                .build()).collect(Collectors.toList())
                        : List.of())
                .nutritionalInfo(r.getNutritionalInfo() != null
                        ? toNutritionalInfoDto(r.getNutritionalInfo())
                        : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private NutritionalInfoDto toNutritionalInfoDto(NutritionalInfo n) {
        return NutritionalInfoDto.builder()
                .id(n.getId())
                .caloriesPerServing(n.getCaloriesPerServing())
                .proteinGrams(n.getProteinGrams())
                .fatGrams(n.getFatGrams())
                .carbohydrateGrams(n.getCarbohydrateGrams())
                .fiberGrams(n.getFiberGrams())
                .sugarGrams(n.getSugarGrams())
                .sodiumMg(n.getSodiumMg())
                .build();
    }
}
