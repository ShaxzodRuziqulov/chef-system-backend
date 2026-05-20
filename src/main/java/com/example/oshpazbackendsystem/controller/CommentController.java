package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.CommentRequest;
import com.example.oshpazbackendsystem.dto.response.CommentDto;
import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.CommentService;
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
@RequestMapping("/api/recipes/{recipeId}/comments")
@RequiredArgsConstructor
@Tag(name = "Izohlar")
public class CommentController {

    private final CommentService service;

    /**
     * GET /api/recipes/{recipeId}/comments  →  sahifalangan izohlar ro'yxati
     */
    @GetMapping
    @Operation(summary = "Retsept izohlarini ko'rish")
    public ResponseEntity<ApiResponse<PageResponse<CommentDto>>> getComments(
            @PathVariable Long recipeId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.getComments(recipeId, pageable)));
    }

    /**
     * POST /api/recipes/{recipeId}/comments  →  yangi izoh qo'shish
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retseptga izoh qo'shish")
    public ResponseEntity<ApiResponse<CommentDto>> addComment(
            @PathVariable Long recipeId,
            @Valid @RequestBody CommentRequest request) {
        CommentDto dto = service.addComment(recipeId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(dto));
    }

    /**
     * DELETE /api/recipes/{recipeId}/comments/{commentId}  →  izohni o'chirish
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Izohni o'chirish (soft-delete)")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long recipeId,
            @PathVariable Long commentId) {
        service.deleteComment(recipeId, commentId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
