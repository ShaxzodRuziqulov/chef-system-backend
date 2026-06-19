package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.exception.ApiResponse;
import com.example.oshpazbackendsystem.service.GcsStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.upload.max-img-size}")
    private long maxImgSize;

    @Value("#{'${app.upload.allowed-img-types}'.split(',')}")
    private Set<String> allowedImg;

    @Value("${app.upload.max-video-size}")
    private long maxVideoSize;

    @Value("#{'${app.upload.allowed-video-types}'.split(',')}")
    private Set<String> allowedVideo;

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Rasm yuklash (authenticated)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new IllegalArgumentException("Fayl bo'sh");
        if (file.getSize() > maxImgSize)
            throw new IllegalArgumentException("Rasm hajmi 5 MB dan oshmasligi kerak");
        if (!allowedImg.contains(file.getContentType()))
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
        if (file.getSize() > maxVideoSize)
            throw new IllegalArgumentException("Video hajmi 200 MB dan oshmasligi kerak");
        if (!allowedVideo.contains(file.getContentType()))
            throw new IllegalArgumentException("Faqat MP4, WebM, MOV, AVI formatlari qabul qilinadi");

        String url = storageService.uploadVideo(file);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url)));
    }
}
