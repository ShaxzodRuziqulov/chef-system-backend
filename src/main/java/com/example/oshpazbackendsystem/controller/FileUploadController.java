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

    private static final long        MAX_IMG_SIZE   = 5   * 1024 * 1024L;
    private static final long        MAX_VIDEO_SIZE = 200 * 1024 * 1024L;
    private static final Set<String> ALLOWED_IMG    = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_VIDEO  = Set.of(
            "video/mp4", "video/webm", "video/quicktime", "video/x-msvideo"
    );

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Rasm yuklash (authenticated)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new IllegalArgumentException("Fayl bo'sh");
        if (file.getSize() > MAX_IMG_SIZE)
            throw new IllegalArgumentException("Rasm hajmi 5 MB dan oshmasligi kerak");
        if (!ALLOWED_IMG.contains(file.getContentType()))
            throw new IllegalArgumentException("Faqat JPEG, PNG, WEBP, GIF formatlari qabul qilinadi");

        String url = storageService.upload(file);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url)));
    }

    @PostMapping("/video")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Video yuklash (authenticated)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadVideo(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new IllegalArgumentException("Fayl bo'sh");
        if (file.getSize() > MAX_VIDEO_SIZE)
            throw new IllegalArgumentException("Video hajmi 200 MB dan oshmasligi kerak");
        if (!ALLOWED_VIDEO.contains(file.getContentType()))
            throw new IllegalArgumentException("Faqat MP4, WebM, MOV, AVI formatlari qabul qilinadi");

        String url = storageService.uploadVideo(file);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url)));
    }
}
