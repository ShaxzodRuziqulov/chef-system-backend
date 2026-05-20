package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RatingRequest {

    @NotNull(message = "Baho kiritilishi shart")
    @Min(value = 1, message = "Baho 1 dan kam bo'lmasligi kerak")
    @Max(value = 5, message = "Baho 5 dan ko'p bo'lmasligi kerak")
    private Integer score;
}
