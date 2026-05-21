package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.AdminUserUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Foydalanuvchilar")
public class UserController {

    private final UserService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Barcha foydalanuvchilar (sahifalangan) — ADMIN")
    public ResponseEntity<ApiResponse<Page<UserDto>>> findAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    String search) {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll(page, size, search)));
    }

    @GetMapping("/count-active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Faol foydalanuvchilar soni — ADMIN")
    public ResponseEntity<ApiResponse<Long>> countActive() {
        return ResponseEntity.ok(ApiResponse.ok(service.countActive()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Foydalanuvchi profili")
    public ResponseEntity<ApiResponse<UserDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Foydalanuvchini tahrirlash — ADMIN")
    public ResponseEntity<ApiResponse<UserDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUserUpdateRequest req) {
        // Agar newPassword to'ldirilgan bo'lsa — parolni ham yangilayik
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            service.resetPasswordByAdmin(id, req.getNewPassword());
        }
        return ResponseEntity.ok(ApiResponse.ok(service.updateByAdmin(id, req)));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Foydalanuvchini bloklash — ADMIN")
    public ResponseEntity<ApiResponse<UserDto>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.deleteById(id)));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Foydalanuvchini faollashtirish — ADMIN")
    public ResponseEntity<ApiResponse<UserDto>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.activateById(id)));
    }
}
