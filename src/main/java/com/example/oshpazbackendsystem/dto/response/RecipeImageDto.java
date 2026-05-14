package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageDto {

    private Long id;
    private String imageUrl;
    private boolean primaryImage;
    private String caption;
    private Integer orderIndex;
}
