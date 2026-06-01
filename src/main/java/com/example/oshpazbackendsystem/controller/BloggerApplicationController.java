package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.BloggerApplicationReviewRequest;
import com.example.oshpazbackendsystem.dto.response.BloggerApplicationDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.BloggerApplicationService;
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
@RequestMapping("/api/blogger-applications")
@RequiredArgsConstructor
@Tag(name = "Blogger Arizalari")
public class BloggerApplicationController {

    private final BloggerApplicationService service;

    // ── USER: ariza yuborish ─────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Oshpaz bo'lish uchun ariza yuborish")
    public ResponseEntity<ApiResponse<BloggerApplicationDto>> apply() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(service.apply()));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "O'z arizamning holati")
    public ResponseEntity<ApiResponse<BloggerApplicationDto>> getMyApplication() {
        return ResponseEntity.ok(ApiResponse.ok(service.getMyApplication()));
    }

    // ── ADMIN: arizalarni boshqarish ─────────────────────────────────────────

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kutayotgan arizalar — ADMIN")
    public ResponseEntity<ApiResponse<Page<BloggerApplicationDto>>> getPending(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.getPending(pageable)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Barcha arizalar — ADMIN")
    public ResponseEntity<ApiResponse<Page<BloggerApplicationDto>>> getAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll(pageable)));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Arizani tasdiqlash yoki rad etish — ADMIN")
    public ResponseEntity<ApiResponse<BloggerApplicationDto>> review(
            @PathVariable Long id,
            @Valid @RequestBody BloggerApplicationReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.review(id, request)));
    }
}
