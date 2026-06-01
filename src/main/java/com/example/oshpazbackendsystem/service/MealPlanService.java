package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.MealPlanCreateRequest;
import com.example.oshpazbackendsystem.dto.MealPlanEntryRequest;
import com.example.oshpazbackendsystem.dto.MealPlanUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.MealPlanEntryDto;
import com.example.oshpazbackendsystem.dto.response.MealPlanResponse;
import com.example.oshpazbackendsystem.entity.MealPlan;
import com.example.oshpazbackendsystem.entity.MealPlanEntry;
import com.example.oshpazbackendsystem.entity.Recipe;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.PlanStatus;
import com.example.oshpazbackendsystem.exception.NotFoundException;
import com.example.oshpazbackendsystem.repository.MealPlanEntryRepository;
import com.example.oshpazbackendsystem.repository.MealPlanRepository;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanEntryRepository entryRepository;
    private final RecipeRepository recipeRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<MealPlanResponse> findMyPlans(Pageable pageable) {
        User user = currentUserService.getCurrentUser();
        return mealPlanRepository.findByUserIdOrderByWeekStartDateDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MealPlanResponse findById(Long id) {
        MealPlan plan = mealPlanRepository.findByIdWithEntries(id)
                .orElseThrow(() -> new NotFoundException("MEAL_PLAN_NOT_FOUND", "Reja topilmadi: " + id));
        checkOwnership(plan);
        return toResponse(plan);
    }

    public MealPlanResponse create(MealPlanCreateRequest request) {
        User user = currentUserService.getCurrentUser();

        // Hafta boshlanish sana bo'yicha tekshiruv
        LocalDate startDate = request.getWeekStartDate();
        LocalDate endDate = startDate.plusDays(6);

        MealPlan plan = MealPlan.builder()
                .user(user)
                .name(request.getName())
                .weekStartDate(startDate)
                .weekEndDate(endDate)
                .notes(request.getNotes())
                .status(PlanStatus.DRAFT)
                .build();

        return toResponse(mealPlanRepository.save(plan));
    }

    public MealPlanResponse addEntry(Long planId, MealPlanEntryRequest request) {
        MealPlan plan = getMealPlan(planId);
        checkOwnership(plan);

        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + request.getRecipeId()));

        MealPlanEntry entry = MealPlanEntry.builder()
                .mealPlan(plan)
                .recipe(recipe)
                .dayOfWeek(request.getDayOfWeek())
                .mealType(request.getMealType())
                .servings(request.getServings())
                .notes(request.getNotes())
                .build();

        plan.getEntries().add(entry);
        plan.setUpdatedAt(LocalDateTime.now());
        return toResponse(mealPlanRepository.save(plan));
    }

    public MealPlanResponse removeEntry(Long planId, Long entryId) {
        MealPlan plan = getMealPlan(planId);
        checkOwnership(plan);

        MealPlanEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("ENTRY_NOT_FOUND", "Yozuv topilmadi: " + entryId));

        plan.getEntries().remove(entry);
        plan.setUpdatedAt(LocalDateTime.now());
        return toResponse(mealPlanRepository.save(plan));
    }

    public MealPlanResponse update(Long id, MealPlanUpdateRequest request) {
        MealPlan plan = getMealPlan(id);
        checkOwnership(plan);
        plan.setName(request.getName());
        plan.setNotes(request.getNotes());
        return toResponse(mealPlanRepository.save(plan));
    }

    public MealPlanResponse activate(Long id) {
        MealPlan plan = getMealPlan(id);
        checkOwnership(plan);
        plan.setStatus(PlanStatus.ACTIVE);
        return toResponse(mealPlanRepository.save(plan));
    }

    public void delete(Long id) {
        MealPlan plan = getMealPlan(id);
        checkOwnership(plan);
        mealPlanRepository.delete(plan); // DB CASCADE: shopping_lists ? shopping_list_items avtomatik o'chadi
    }

    private MealPlan getMealPlan(Long id) {
        return mealPlanRepository.findByIdWithEntries(id)
                .orElseThrow(() -> new NotFoundException("MEAL_PLAN_NOT_FOUND", "Reja topilmadi: " + id));
    }

    private void checkOwnership(MealPlan plan) {
        User current = currentUserService.getCurrentUser();
        if (!plan.getUser().getId().equals(current.getId())) {
            throw new IllegalStateException("Bu rejaga kirish uchun ruxsat yo'q");
        }
    }

    private MealPlanResponse toResponse(MealPlan p) {
        List<MealPlanEntryDto> entries = p.getEntries().stream()
                .map(e -> MealPlanEntryDto.builder()
                        .id(e.getId())
                        .recipeId(e.getRecipe().getId())
                        .recipeTitleUz(e.getRecipe().getTitleUz())
                        .recipeTitleRu(e.getRecipe().getTitleRu())
                        .recipeImageUrl(e.getRecipe().getImageUrl())
                        .dayOfWeek(e.getDayOfWeek())
                        .mealType(e.getMealType())
                        .servings(e.getServings())
                        .notes(e.getNotes())
                        .build())
                .collect(Collectors.toList());

        return MealPlanResponse.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .userFullName(p.getUser().getFullName())
                .name(p.getName())
                .weekStartDate(p.getWeekStartDate())
                .weekEndDate(p.getWeekEndDate())
                .status(p.getStatus())
                .notes(p.getNotes())
                .entries(entries)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
