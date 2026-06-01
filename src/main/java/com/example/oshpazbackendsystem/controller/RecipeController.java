package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.RecipeCreateRequest;
import com.example.oshpazbackendsystem.dto.RecipeUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeDto;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Retseptlar")
public class RecipeController {

    private final RecipeService service;

    @GetMapping
    @Operation(summary = "Barcha ochiq retseptlar")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findAll(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findAll(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retsept tafsilotlari")
    public ResponseEntity<ApiResponse<RecipeDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Ko'p tilli qidiruv (uz/ru/eng)")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.search(keyword, pageable))));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Kategoriya bo'yicha retseptlar")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findByCategory(categoryId, pageable))));
    }

    @GetMapping("/difficulty/{level}")
    @Operation(summary = "Qiyinlik darajasi bo'yicha (EASY / MEDIUM / HARD / EXPERT)")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findByDifficulty(
            @PathVariable DifficultyLevel level,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findByDifficulty(level, pageable))));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mening retseptlarim")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findMyRecipes(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findMyRecipes(pageable))));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Yangi retsept yaratish — faqat BLOGGER va ADMIN")
    public ResponseEntity<ApiResponse<RecipeDto>> create(@Valid @RequestBody RecipeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(service.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Retseptni yangilash (to'liq) — faqat BLOGGER va ADMIN")
    public ResponseEntity<ApiResponse<RecipeDto>> update(@PathVariable Long id,
                                             @Valid @RequestBody RecipeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Retseptni o'chirish — faqat BLOGGER va ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Ko'rishlar sonini oshirish")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        service.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }
}
