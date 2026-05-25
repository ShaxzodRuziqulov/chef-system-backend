package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatsDto {

    private long totalRecipes;
    private long totalUsers;
    private long totalCategories;
    private List<TopRecipeDto> topRecipes;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRecipeDto {
        private Long id;
        private String titleUz;
        private String titleRu;
        private String titleEng;
        private String imageUrl;
        private Long viewCount;
        private Double averageRating;
    }
}
