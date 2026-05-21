package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.GcsStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "Fayl yuklash")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final GcsStorageService storageService;

    private static final long     MAX_SIZE = 5 * 1024 * 1024L;
    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Rasm yuklash (authenticated)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // Validatsiya
        if (file.isEmpty())
            throw new IllegalArgumentException("Fayl bo'sh");
        if (file.getSize() > MAX_SIZE)
            throw new IllegalArgumentException("Fayl hajmi 5 MB dan oshmasligi kerak");
        if (!ALLOWED.contains(file.getContentType()))
            throw new IllegalArgumentException("Faqat JPEG, PNG, WEBP, GIF formatlari qabul qilinadi");

        String url = storageService.upload(file);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url)));
    }
}
