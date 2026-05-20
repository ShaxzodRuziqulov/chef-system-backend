package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.RatingRequest;
import com.example.oshpazbackendsystem.dto.response.RatingResultDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ratings")
@RequiredArgsConstructor
@Tag(name = "Baholar")
public class RatingController {

    private final RatingService service;

    /**
     * POST /api/recipes/{recipeId}/ratings
     * Retseptga baho qo'yish yoki yangilash (1-5 yulduz)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retseptga baho qo'yish yoki yangilash")
    public ResponseEntity<ApiResponse<RatingResultDto>> rate(
            @PathVariable Long recipeId,
            @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.rate(recipeId, request)));
    }

    /**
     * GET /api/recipes/{recipeId}/ratings/me
     * Mening bahom + o'rtacha ko'rsatkich
     */
    @GetMapping("/me")
    @Operation(summary = "Mening bahom va retseptning o'rtacha bahosi")
    public ResponseEntity<ApiResponse<RatingResultDto>> getMyRating(
            @PathVariable Long recipeId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getMyRating(recipeId)));
    }
}
