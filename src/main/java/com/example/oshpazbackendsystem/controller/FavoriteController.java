package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "Sevimlilar")
public class FavoriteController {

    private final FavoriteService service;

    /**
     * POST /api/favorites/{recipeId}  →  toggle (qo'shish/o'chirish)
     * Qaytaradi: { "favorited": true/false }
     */
    @PostMapping("/{recipeId}")
    @Operation(summary = "Sevimlilarga qo'shish yoki o'chirish (toggle)")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggle(
            @PathVariable Long recipeId) {
        boolean favorited = service.toggle(recipeId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("favorited", favorited)));
    }

    /**
     * GET /api/favorites/ids  →  foydalanuvchining barcha sevimli ID lari
     * Frontend karta ustida ♥ belgisini ko'rsatish uchun
     */
    @GetMapping("/ids")
    @Operation(summary = "Sevimli retseptlar ID to'plami")
    public ResponseEntity<ApiResponse<Set<Long>>> getFavoriteIds() {
        return ResponseEntity.ok(ApiResponse.ok(service.getFavoriteIds()));
    }

    /**
     * GET /api/favorites  →  sahifalangan sevimli retseptlar
     */
    @GetMapping
    @Operation(summary = "Saqlangan retseptlar ro'yxati (sahifalangan)")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> getFavorites(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.getFavorites(pageable))));
    }
}
