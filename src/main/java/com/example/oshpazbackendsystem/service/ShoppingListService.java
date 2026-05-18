package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.ShoppingListItemStatusRequest;
import com.example.oshpazbackendsystem.dto.response.ShoppingListDto;
import com.example.oshpazbackendsystem.dto.response.ShoppingListItemDto;
import com.example.oshpazbackendsystem.entity.*;
import com.example.oshpazbackendsystem.entity.enums.ShoppingItemStatus;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.repository.MealPlanRepository;
import com.example.oshpazbackendsystem.repository.ShoppingListItemRepository;
import com.example.oshpazbackendsystem.repository.ShoppingListRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository itemRepository;
    private final MealPlanRepository mealPlanRepository;
    private final CurrentUserService currentUserService;

    // ── O'qish ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ShoppingListDto> findMyLists(Pageable pageable) {
        User user = currentUserService.getCurrentUser();
        return shoppingListRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ShoppingListDto findById(Long id) {
        ShoppingList list = shoppingListRepository.findByIdWithItems(id)
                .orElseThrow(() -> new NotFoundException("SHOPPING_LIST_NOT_FOUND", "Xarid ro'yxati topilmadi: " + id));
        checkOwnership(list);
        return toDto(list);
    }

    // ── Reja asosida avtomatik yaratish ─────────────────────────────────────

    public ShoppingListDto generateFromMealPlan(Long mealPlanId) {
        User user = currentUserService.getCurrentUser();

        MealPlan plan = mealPlanRepository.findByIdWithEntries(mealPlanId)
                .orElseThrow(() -> new NotFoundException("MEAL_PLAN_NOT_FOUND", "Reja topilmadi: " + mealPlanId));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bu rejaga kirish uchun ruxsat yo'q");
        }

        // Barcha entry lardan ingredientlarni yig'ish
        Map<Long, IngredientAccumulator> accumulatorMap = new HashMap<>();
        for (MealPlanEntry entry : plan.getEntries()) {
            Recipe recipe = entry.getRecipe();
            int servingsMultiplier = entry.getServings();
            for (RecipeIngredient ri : recipe.getIngredients()) {
                Long ingredientId = ri.getIngredient().getId();
                double scaledAmount = ri.getAmount() * servingsMultiplier;
                accumulatorMap.merge(ingredientId,
                        new IngredientAccumulator(ri.getIngredient(), scaledAmount, ri.getUnit()),
                        (existing, newVal) -> { existing.totalAmount += newVal.totalAmount; return existing; });
            }
        }

        List<ShoppingListItem> items = new ArrayList<>();

        // Mavjud ro'yxatni yangilaymiz, yo'q bo'lsa yangisini yaratamiz
        ShoppingList shoppingList = shoppingListRepository.findByMealPlanId(mealPlanId)
                .orElseGet(() -> ShoppingList.builder()
                        .user(user)
                        .mealPlan(plan)
                        .name(plan.getName() + " — xarid ro'yxati")
                        .completed(false)
                        .build());

        shoppingList.setName(plan.getName() + " — xarid ro'yxati");
        shoppingList.setCompleted(false);
        shoppingList.setGeneratedAt(LocalDateTime.now());
        shoppingList.getItems().clear();   // orphanRemoval=true → eski itemlar o'chadi

        for (IngredientAccumulator acc : accumulatorMap.values()) {
            items.add(ShoppingListItem.builder()
                    .shoppingList(shoppingList)
                    .ingredient(acc.ingredient)
                    .amount(acc.totalAmount)
                    .unit(acc.unit)
                    .status(ShoppingItemStatus.PENDING)
                    .build());
        }
        shoppingList.getItems().addAll(items);

        return toDto(shoppingListRepository.save(shoppingList));
    }

    // ── Mahsulot holatini yangilash ──────────────────────────────────────────

    public ShoppingListDto updateItemStatus(Long listId, Long itemId,
                                            ShoppingListItemStatusRequest request) {
        ShoppingList list = shoppingListRepository.findByIdWithItems(listId)
                .orElseThrow(() -> new NotFoundException("SHOPPING_LIST_NOT_FOUND", "Xarid ro'yxati topilmadi: " + listId));
        checkOwnership(list);

        ShoppingListItem item = list.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("ITEM_NOT_FOUND", "Element topilmadi: " + itemId));

        item.setStatus(request.getStatus());

        // Barcha mahsulot sotib olingan bo'lsa, ro'yxatni tugallangan deb belgilash
        boolean allPurchased = list.getItems().stream()
                .allMatch(i -> i.getStatus() == ShoppingItemStatus.PURCHASED);
        list.setCompleted(allPurchased);

        return toDto(shoppingListRepository.save(list));
    }

    public void delete(Long id) {
        ShoppingList list = shoppingListRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SHOPPING_LIST_NOT_FOUND", "Xarid ro'yxati topilmadi: " + id));
        checkOwnership(list);
        shoppingListRepository.delete(list);
    }

    // ── Ichki metodlar ───────────────────────────────────────────────────────

    private void checkOwnership(ShoppingList list) {
        User current = currentUserService.getCurrentUser();
        if (!list.getUser().getId().equals(current.getId())) {
            throw new IllegalStateException("Bu ro'yxatga kirish uchun ruxsat yo'q");
        }
    }

    /** Ingredient miqdorini yig'ish uchun yordamchi sinf */
    private static class IngredientAccumulator {
        Ingredient ingredient;
        double totalAmount;
        com.example.oshpazbackendsystem.entity.enums.MeasurementUnit unit;

        IngredientAccumulator(Ingredient ingredient, double totalAmount,
                              com.example.oshpazbackendsystem.entity.enums.MeasurementUnit unit) {
            this.ingredient = ingredient;
            this.totalAmount = totalAmount;
            this.unit = unit;
        }
    }

    // ── Manual mapping ───────────────────────────────────────────────────────

    private ShoppingListDto toDto(ShoppingList sl) {
        List<ShoppingListItemDto> items = sl.getItems().stream()
                .map(i -> ShoppingListItemDto.builder()
                        .id(i.getId())
                        .ingredientId(i.getIngredient().getId())
                        .ingredientNameUz(i.getIngredient().getNameUz())
                        .ingredientNameRu(i.getIngredient().getNameRu())
                        .ingredientNameEng(i.getIngredient().getNameEng())
                        .amount(i.getAmount())
                        .unit(i.getUnit())
                        .status(i.getStatus())
                        .estimatedPrice(i.getEstimatedPrice())
                        .notes(i.getNotes())
                        .grocerySection(i.getGrocerySection())
                        .build())
                .collect(Collectors.toList());

        return ShoppingListDto.builder()
                .id(sl.getId())
                .userId(sl.getUser().getId())
                .mealPlanId(sl.getMealPlan() != null ? sl.getMealPlan().getId() : null)
                .mealPlanName(sl.getMealPlan() != null ? sl.getMealPlan().getName() : null)
                .generatedAt(sl.getGeneratedAt())
                .name(sl.getName())
                .completed(sl.isCompleted())
                .items(items)
                .createdAt(sl.getCreatedAt())
                .updatedAt(sl.getUpdatedAt())
                .build();
    }
}
