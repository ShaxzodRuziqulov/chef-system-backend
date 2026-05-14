package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanCreateRequest {

    @NotBlank(message = "Reja nomi bo'sh bo'lmasligi kerak")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Hafta boshlanish sanasi kiritilishi shart")
    private LocalDate weekStartDate;

    private String notes;
}
