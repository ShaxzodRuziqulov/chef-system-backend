package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
    name = "recipes",
    indexes = {
        @Index(name = "idx_recipes_title_uz",    columnList = "title_uz"),
        @Index(name = "idx_recipes_category_id", columnList = "category_id"),
        @Index(name = "idx_recipes_author_id",   columnList = "author_id"),
        @Index(name = "idx_recipes_difficulty",  columnList = "difficulty_level"),
        @Index(name = "idx_recipes_deleted",     columnList = "deleted"),
        @Index(name = "idx_recipes_visible",     columnList = "visible")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String titleUz;

    @Size(max = 200)
    @Column(length = 200)
    private String titleRu;

    @Size(max = 200)
    @Column(length = 200)
    private String titleEng;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(1)
    @Column(nullable = false)
    private Integer prepTimeMinutes;

    @Min(0)
    @Column(nullable = false)
    private Integer cookTimeMinutes;

    @Min(1)
    @Column(nullable = false)
    private Integer servings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String videoUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean visible = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Builder.Default
    private Double averageRating = 0.0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "recipe_tags",
        joinColumns        = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepNumber ASC")
    @Builder.Default
    private List<RecipeStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<RecipeImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private NutritionalInfo nutritionalInfo;
}
