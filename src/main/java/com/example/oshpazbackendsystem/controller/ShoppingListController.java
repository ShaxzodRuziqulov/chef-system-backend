package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.ShoppingListItemStatusRequest;
import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.dto.response.ShoppingListDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Xarid ro'yxati")
public class ShoppingListController {

    private final ShoppingListService service;

    @GetMapping
    @Operation(summary = "Mening xarid ro'yxatlarim")
    public ResponseEntity<ApiResponse<PageResponse<ShoppingListDto>>> findMyLists(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findMyLists(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xarid ro'yxati tafsilotlari")
    public ResponseEntity<ApiResponse<ShoppingListDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @PostMapping("/generate/{mealPlanId}")
    @Operation(summary = "Ovqat rejasidan avtomatik xarid ro'yxati yaratish")
    public ResponseEntity<ApiResponse<ShoppingListDto>> generate(@PathVariable Long mealPlanId) {
        return ResponseEntity.ok(ApiResponse.ok(service.generateFromMealPlan(mealPlanId)));
    }

    @PatchMapping("/{id}/items/{itemId}")
    @Operation(summary = "Mahsulot holatini yangilash (PENDING / PURCHASED / SKIPPED)")
    public ResponseEntity<ApiResponse<ShoppingListDto>> updateItemStatus(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody ShoppingListItemStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateItemStatus(id, itemId, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xarid ro'yxatini o'chirish")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
