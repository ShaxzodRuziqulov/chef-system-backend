package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Kategoriya nomi (UZ) bo'sh bo'lmasligi kerak")
    @Size(max = 100)
    private String nameUz;

    @Size(max = 100)
    private String nameRu;

    @Size(max = 100)
    private String nameEng;

    @Size(max = 500)
    private String description;

    @Size(max = 500)
    private String iconUrl;

    @Size(max = 10)
    private String colorCode;
}
