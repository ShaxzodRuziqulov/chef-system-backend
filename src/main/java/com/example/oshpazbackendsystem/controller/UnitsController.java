package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.response.MeasurementUnitDto;
import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import com.example.oshpazbackendsystem.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/units")
@Tag(name = "O'lchov birliklari")
public class UnitsController {

    @GetMapping
    @Operation(summary = "Barcha o'lchov birliklarini qaytaradi (3 tilda + konvertatsiya meta)")
    public ResponseEntity<ApiResponse<List<MeasurementUnitDto>>> getAll() {
        List<MeasurementUnitDto> units = Arrays.stream(MeasurementUnit.values())
                .map(MeasurementUnitDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(units));
    }
}
