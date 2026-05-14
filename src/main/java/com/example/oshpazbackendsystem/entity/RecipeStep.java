package com.example.oshpazbackendsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "recipe_steps",
    indexes = {
        @Index(name = "idx_recipe_steps_recipe_id", columnList = "recipe_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Min(1)
    @Column(nullable = false)
    private Integer stepNumber;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String instruction;

    @Column(length = 500)
    private String imageUrl;

    @Min(0)
    private Integer durationMinutes;
}
