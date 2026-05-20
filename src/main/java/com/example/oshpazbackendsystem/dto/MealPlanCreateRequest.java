package com.example.oshpazbackendsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MealPlanCreateRequest {

    @NotBlank(message = "Reja nomi bo'sh bo'lmasligi kerak")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Hafta boshlanish sanasi kiritilishi shart")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;

    private String notes;
}
