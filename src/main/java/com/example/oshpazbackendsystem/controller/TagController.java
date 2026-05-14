package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.TagRequest;
import com.example.oshpazbackendsystem.dto.response.TagDto;
import com.example.oshpazbackendsystem.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Teglar", description = "Retsept teglarini boshqarish")
public class TagController {

    private final TagService service;

    @GetMapping
    @Operation(summary = "Barcha teglar")
    public ResponseEntity<List<TagDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID bo'yicha teg")
    public ResponseEntity<TagDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Teg yaratish — faqat ADMIN")
    public ResponseEntity<TagDto> create(@Valid @RequestBody TagRequest request) {
        TagDto dto = TagDto.builder()
                .nameUz(request.getNameUz())
                .nameRu(request.getNameRu())
                .nameEng(request.getNameEng())
                .description(request.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Teg yangilash — faqat ADMIN")
    public ResponseEntity<TagDto> update(@PathVariable Long id,
                                         @Valid @RequestBody TagRequest request) {
        TagDto dto = TagDto.builder()
                .nameUz(request.getNameUz())
                .nameRu(request.getNameRu())
                .nameEng(request.getNameEng())
                .description(request.getDescription())
                .build();
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Teg o'chirish — faqat ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
