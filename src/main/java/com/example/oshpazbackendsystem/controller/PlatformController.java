package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.response.PlatformFeaturesDto;
import com.example.oshpazbackendsystem.dto.response.PlatformStatsDto;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Platform", description = "Platforma umumiy ma'lumotlari (public)")
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping("/stats")
    @Operation(summary = "Platforma statistikasi — jami retseptlar, foydalanuvchilar, top retseptlar")
    public ResponseEntity<ApiResponse<PlatformStatsDto>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(platformService.getStats()));
    }

    @GetMapping("/features")
    @Operation(summary = "Platforma imkoniyatlari — ro'yxatdan o'tmagan va a'zo foydalanuvchilar uchun")
    public ResponseEntity<ApiResponse<PlatformFeaturesDto>> getFeatures() {
        return ResponseEntity.ok(ApiResponse.ok(platformService.getFeatures()));
    }
}
