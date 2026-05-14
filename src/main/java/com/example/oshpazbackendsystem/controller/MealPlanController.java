package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.MealPlanCreateRequest;
import com.example.oshpazbackendsystem.dto.MealPlanEntryRequest;
import com.example.oshpazbackendsystem.dto.response.MealPlanResponse;
import com.example.oshpazbackendsystem.service.MealPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@Tag(name = "Haftalik ovqat rejasi", description = "Ovqat rejasini yaratish va boshqarish")
public class MealPlanController {

    private final MealPlanService service;

    @GetMapping
    @Operation(summary = "Mening barcha rejalarim")
    public ResponseEntity<Page<MealPlanResponse>> findMyPlans(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.findMyPlans(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Reja tafsilotlari")
    public ResponseEntity<MealPlanResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Yangi reja yaratish")
    public ResponseEntity<MealPlanResponse> create(
            @Valid @RequestBody MealPlanCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PostMapping("/{id}/entries")
    @Operation(summary = "Rejaga ovqat qo'shish")
    public ResponseEntity<MealPlanResponse> addEntry(
            @PathVariable Long id,
            @Valid @RequestBody MealPlanEntryRequest request) {
        return ResponseEntity.ok(service.addEntry(id, request));
    }

    @DeleteMapping("/{id}/entries/{entryId}")
    @Operation(summary = "Rejadan ovqatni o'chirish")
    public ResponseEntity<MealPlanResponse> removeEntry(
            @PathVariable Long id,
            @PathVariable Long entryId) {
        return ResponseEntity.ok(service.removeEntry(id, entryId));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Rejani faollashtirish")
    public ResponseEntity<MealPlanResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(service.activate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Rejani o'chirish")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
