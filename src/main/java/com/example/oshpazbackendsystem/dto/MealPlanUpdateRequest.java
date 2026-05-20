package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealPlanUpdateRequest {

    @NotBlank(message = "Reja nomi bo'sh bo'lmasligi kerak")
    @Size(max = 100)
    private String name;

    private String notes;
}
