package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "Fayl yuklash")
@Slf4j
public class FileUploadController {

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    private static final long    MAX_SIZE      = 5 * 1024 * 1024L; // 5 MB
    private static final Set<String> ALLOWED   = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Rasm yuklash (authenticated)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // ── Validatsiya ──────────────────────────────────────────
        String ext = getString(file);
        String fileName  = UUID.randomUUID().toString().replace("-", "") + ext;

        // ── Papkani yaratish va fayl saqlash ─────────────────────
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        log.info("Rasm saqlandi: {}/{}", uploadPath, fileName);

        // ── URL qaytarish: WebMvcConfig /uploads/** → uploadDir papkasiga map qiladi ──
        // uploadDir = "uploads/images" → URL = /uploads/xxx.jpg
        String url = "/uploads/" + fileName;
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url)));
    }

    private static @NonNull String getString(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("Fayl bo'sh");

        if (file.getSize() > MAX_SIZE)
            throw new IllegalArgumentException("Fayl hajmi 5 MB dan oshmasligi kerak");

        if (!ALLOWED.contains(file.getContentType()))
            throw new IllegalArgumentException("Faqat JPEG, PNG, WEBP, GIF formatlari qabul qilinadi");

        // ── Fayl nomini yaratish ─────────────────────────────────
        String original  = file.getOriginalFilename() != null ? file.getOriginalFilename() : "img";
        return original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
    }
}
