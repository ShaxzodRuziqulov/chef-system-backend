package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagRequest {

    @NotBlank(message = "Teg nomi (UZ) bo'sh bo'lmasligi kerak")
    @Size(max = 50)
    private String nameUz;

    @Size(max = 50)
    private String nameRu;

    @Size(max = 50)
    private String nameEng;

    @Size(max = 255)
    private String description;
}
