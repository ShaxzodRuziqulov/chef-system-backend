package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.MealPlanCreateRequest;
import com.example.oshpazbackendsystem.dto.MealPlanEntryRequest;
import com.example.oshpazbackendsystem.dto.response.MealPlanResponse;
import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.MealPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Haftalik ovqat rejasi")
public class MealPlanController {

    private final MealPlanService service;

    @GetMapping
    @Operation(summary = "Mening barcha rejalarim")
    public ResponseEntity<ApiResponse<PageResponse<MealPlanResponse>>> findMyPlans(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findMyPlans(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Reja tafsilotlari")
    public ResponseEntity<ApiResponse<MealPlanResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Yangi reja yaratish")
    public ResponseEntity<ApiResponse<MealPlanResponse>> create(
            @Valid @RequestBody MealPlanCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(service.create(request)));
    }

    @PostMapping("/{id}/entries")
    @Operation(summary = "Rejaga ovqat qo'shish")
    public ResponseEntity<ApiResponse<MealPlanResponse>> addEntry(
            @PathVariable Long id,
            @Valid @RequestBody MealPlanEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.addEntry(id, request)));
    }

    @DeleteMapping("/{id}/entries/{entryId}")
    @Operation(summary = "Rejadan ovqatni o'chirish")
    public ResponseEntity<ApiResponse<MealPlanResponse>> removeEntry(
            @PathVariable Long id,
            @PathVariable Long entryId) {
        return ResponseEntity.ok(ApiResponse.ok(service.removeEntry(id, entryId)));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Rejani faollashtirish")
    public ResponseEntity<ApiResponse<MealPlanResponse>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.activate(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Rejani o'chirish")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
