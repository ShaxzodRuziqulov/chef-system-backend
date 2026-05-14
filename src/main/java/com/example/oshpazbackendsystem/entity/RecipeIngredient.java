package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "recipe_ingredients",
    indexes = {
        @Index(name = "idx_recipe_ingredients_recipe_id",     columnList = "recipe_id"),
        @Index(name = "idx_recipe_ingredients_ingredient_id", columnList = "ingredient_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Min(0)
    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeasurementUnit unit;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;
}
