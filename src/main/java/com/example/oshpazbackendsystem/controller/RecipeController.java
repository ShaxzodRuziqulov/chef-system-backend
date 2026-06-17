package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.BulkImportResultDto;
import com.example.oshpazbackendsystem.dto.RecipeCreateRequest;
import com.example.oshpazbackendsystem.dto.RecipeUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeDto;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import java.util.List;
import com.example.oshpazbackendsystem.service.RecipeService;
import com.example.oshpazbackendsystem.service.UserRecipeImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Retseptlar")
public class RecipeController {

    private final RecipeService service;
    private final UserRecipeImportService importService;

    @GetMapping
    @Operation(summary = "Barcha ochiq retseptlar")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findAll(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findAll(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retsept tafsilotlari")
    public ResponseEntity<ApiResponse<RecipeDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Ko'p tilli qidiruv (uz/ru/eng)")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.search(keyword, pageable))));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Kategoriya bo'yicha retseptlar")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findByCategory(categoryId, pageable))));
    }

    @GetMapping("/difficulty/{level}")
    @Operation(summary = "Qiyinlik darajasi bo'yicha (EASY / MEDIUM / HARD / EXPERT)")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findByDifficulty(
            @PathVariable DifficultyLevel level,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findByDifficulty(level, pageable))));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mening retseptlarim")
    public ResponseEntity<ApiResponse<PageResponse<RecipeDto>>> findMyRecipes(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(service.findMyRecipes(pageable))));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Yangi retsept yaratish — faqat BLOGGER va ADMIN")
    public ResponseEntity<ApiResponse<RecipeDto>> create(@Valid @RequestBody RecipeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(service.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Retseptni yangilash (to'liq) — faqat BLOGGER va ADMIN")
    public ResponseEntity<ApiResponse<RecipeDto>> update(@PathVariable Long id,
                                             @Valid @RequestBody RecipeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Retseptni o'chirish — faqat BLOGGER va ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/similar")
    @Operation(summary = "O'xshash retseptlar (bir xil kategoriya, reyting bo'yicha)")
    public ResponseEntity<ApiResponse<List<RecipeDto>>> findSimilar(
            @PathVariable Long id,
            @RequestParam(value = "limit", defaultValue = "6") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(service.findSimilar(id, Math.min(limit, 12))));
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Ko'rishlar sonini oshirish")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        service.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    // ── Excel import / export ────────────────────────────────────────────────

    private static final long MAX_EXCEL_BYTES = 10L * 1024 * 1024; // 10 MB
    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Exceldan retsept yuklash — BLOGGER va ADMIN",
               description = "3 varaqli shablon. mode=SKIP (default) — dublikatni o'tkazib yuborish | mode=UPDATE — mavjudni yangilash (faqat ADMIN)")
    public ResponseEntity<ApiResponse<BulkImportResultDto>> importRecipes(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "SKIP") String mode) throws Exception {

        if (file.isEmpty())
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BulkImportResultDto>builder()
                            .success(false).status(400).message("Fayl bo'sh").build());

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx"))
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BulkImportResultDto>builder()
                            .success(false).status(400).message("Faqat .xlsx format qabul qilinadi").build());

        if (file.getSize() > MAX_EXCEL_BYTES)
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BulkImportResultDto>builder()
                            .success(false).status(400).message("Excel fayl 10 MB dan oshmasligi kerak").build());

        return ResponseEntity.ok(ApiResponse.ok(importService.importFromExcel(file, mode)));
    }

    @GetMapping("/import/template")
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Excel shablon yuklab olish — BLOGGER va ADMIN",
               description = "lang=uz (default) | lang=ru | lang=en")
    public ResponseEntity<byte[]> importTemplate(
            @RequestParam(value = "lang", defaultValue = "uz") String lang) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"retsept_shablon.xlsx\"")
                .contentType(XLSX)
                .body(importService.generateTemplate(lang));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('BLOGGER', 'ADMIN')")
    @Operation(summary = "Retseptlarni Excel ga eksport qilish — BLOGGER va ADMIN",
               description = "BLOGGER — faqat o'z retseptlarini, ADMIN — barcha retseptlarni eksport qiladi. Natija import shabloni bilan mos keladi.")
    public ResponseEntity<byte[]> exportRecipes() throws Exception {
        String filename = "retseptlar_" + java.time.LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(XLSX)
                .body(importService.exportToExcel());
    }
}
