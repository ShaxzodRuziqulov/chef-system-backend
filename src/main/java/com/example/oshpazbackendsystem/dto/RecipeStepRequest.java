package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepRequest {

    @NotNull(message = "Bosqich raqami kiritilishi shart")
    @Min(value = 1, message = "Bosqich raqami 1 dan boshlanishi kerak")
    private Integer stepNumber;

    @NotBlank(message = "Bosqich tavsifi bo'sh bo'lmasligi kerak")
    private String instruction;

    @Size(max = 500)
    private String imageUrl;

    @Min(value = 0)
    private Integer durationMinutes;
}
