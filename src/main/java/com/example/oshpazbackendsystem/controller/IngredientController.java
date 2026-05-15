package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.IngredientRequest;
import com.example.oshpazbackendsystem.dto.response.IngredientDto;
import com.example.oshpazbackendsystem.service.IngredientService;
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
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredientlar")
public class IngredientController {

    private final IngredientService service;

    @GetMapping
    @Operation(summary = "Barcha ingredientlar")
    public ResponseEntity<Page<IngredientDto>> findAll(
            @PageableDefault(size = 20, sort = "nameUz") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID bo'yicha ingredient")
    public ResponseEntity<IngredientDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ingredient yaratish — ADMIN")
    public ResponseEntity<IngredientDto> create(@Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ingredient yangilash — ADMIN")
    public ResponseEntity<IngredientDto> update(@PathVariable Long id,
                                                 @Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ingredient o'chirish — ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
