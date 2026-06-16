package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import com.example.oshpazbackendsystem.entity.enums.IngredientCategory;
import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
    name = "ingredients",
    indexes = {
        @Index(name = "idx_ingredients_name_uz", columnList = "name_uz")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nameUz;

    @Size(max = 100)
    @Column(length = 100)
    private String nameRu;

    @Size(max = 100)
    @Column(length = 100)
    private String nameEng;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MeasurementUnit defaultUnit;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private IngredientCategory category;

    private Double caloriesPer100g;

    @Column(nullable = false)
    @Builder.Default
    private boolean allergen = false;
}
