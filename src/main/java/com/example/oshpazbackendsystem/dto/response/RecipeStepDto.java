package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepDto {

    private Long id;
    private Integer stepNumber;
    private String instruction;
    private String imageUrl;
    private Integer durationMinutes;
}
