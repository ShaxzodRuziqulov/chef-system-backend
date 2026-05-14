package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.RecipeCreateRequest;
import com.example.oshpazbackendsystem.dto.RecipeUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.RecipeDto;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@Tag(name = "Retseptlar", description = "Retseptlarni yaratish, qidirish va boshqarish")
public class RecipeController {

    private final RecipeService service;

    @GetMapping
    @Operation(summary = "Barcha ochiq retseptlar (sahifalash)")
    public ResponseEntity<Page<RecipeDto>> findAll(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retseptni to'liq ma'lumotlari bilan olish")
    public ResponseEntity<RecipeDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Ko'p tilli qidiruv (uz/ru/eng)")
    public ResponseEntity<Page<RecipeDto>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.search(keyword, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Kategoriya bo'yicha retseptlar")
    public ResponseEntity<Page<RecipeDto>> findByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.findByCategory(categoryId, pageable));
    }

    @GetMapping("/difficulty/{level}")
    @Operation(summary = "Qiyinlik darajasi bo'yicha retseptlar")
    public ResponseEntity<Page<RecipeDto>> findByDifficulty(
            @PathVariable DifficultyLevel level,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.findByDifficulty(level, pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mening retseptlarim")
    public ResponseEntity<Page<RecipeDto>> findMyRecipes(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.findMyRecipes(pageable));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Yangi retsept yaratish")
    public ResponseEntity<RecipeDto> create(@Valid @RequestBody RecipeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retseptni qisman yangilash")
    public ResponseEntity<RecipeDto> update(@PathVariable Long id,
                                             @Valid @RequestBody RecipeUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retseptni o'chirish (soft delete)")
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
