package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.ShoppingListItemStatusRequest;
import com.example.oshpazbackendsystem.dto.response.ShoppingListDto;
import com.example.oshpazbackendsystem.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Xarid ro'yxati", description = "Haftalik ovqat rejasidan xarid ro'yxatini yaratish")
public class ShoppingListController {

    private final ShoppingListService service;

    @GetMapping
    @Operation(summary = "Mening xarid ro'yxatlarim")
    public ResponseEntity<Page<ShoppingListDto>> findMyLists(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.findMyLists(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xarid ro'yxati tafsilotlari")
    public ResponseEntity<ShoppingListDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/generate/{mealPlanId}")
    @Operation(
        summary = "Ovqat rejasidan avtomatik xarid ro'yxati yaratish",
        description = "Haftalik rejadagi barcha retseptlar ingredientlari jamlanib, xarid ro'yxati hosil qilinadi"
    )
    public ResponseEntity<ShoppingListDto> generate(@PathVariable Long mealPlanId) {
        return ResponseEntity.ok(service.generateFromMealPlan(mealPlanId));
    }

    @PatchMapping("/{id}/items/{itemId}")
    @Operation(summary = "Mahsulot holatini yangilash (PENDING / PURCHASED / SKIPPED)")
    public ResponseEntity<ShoppingListDto> updateItemStatus(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody ShoppingListItemStatusRequest request) {
        return ResponseEntity.ok(service.updateItemStatus(id, itemId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xarid ro'yxatini o'chirish")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
