package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSummaryResponse {

    private Long id;
    private String titleUz;
    private String titleRu;
    private String titleEng;

    // Kategoriya
    private Long categoryId;
    private String categoryNameUz;

    // Asosiy rasm
    private String imageUrl;

    // Vaqt va porsiya
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer totalTimeMinutes;    // prep + cook
    private Integer servings;
    private DifficultyLevel difficultyLevel;

    // Statistika
    private Double averageRating;
    private Integer ratingCount;
    private Long viewCount;

    // Muallif
    private UUID authorId;
    private String authorFullName;

    // Teglar
    private List<TagResponse> tags;

    private LocalDateTime createdAt;
}
