package com.example.oshpazbackendsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "recipe_images",
    indexes = {
        @Index(name = "idx_recipe_images_recipe_id", columnList = "recipe_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primaryImage = false;

    @Column(length = 255)
    private String caption;

    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;
}
