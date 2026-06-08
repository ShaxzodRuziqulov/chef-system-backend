package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.BulkImportResultDto;
import com.example.oshpazbackendsystem.service.BulkImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/recipes")
@RequiredArgsConstructor
@Tag(name = "Admin – Bulk Import", description = "Exceldan ommaviy retsept yuklash")
public class BulkImportController {

    private final BulkImportService bulkImportService;

    @PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excel fayldan retseptlarni ommaviy yuklash")
    public ResponseEntity<BulkImportResultDto> bulkImport(
            @RequestPart("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().build();
        }

        BulkImportResultDto result = bulkImportService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bulk-import/template")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bo'sh Excel shablonini yuklab olish")
    public ResponseEntity<byte[]> downloadTemplate() throws Exception {
        byte[] bytes = bulkImportService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"retsept_shablon.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
