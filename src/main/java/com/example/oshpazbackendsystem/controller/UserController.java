package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Foydalanuvchi profili")
    public ResponseEntity<ApiResponse<UserDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
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
