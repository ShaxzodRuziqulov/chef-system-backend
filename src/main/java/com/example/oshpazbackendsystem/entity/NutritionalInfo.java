package com.example.oshpazbackendsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nutritional_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false, unique = true)
    private Recipe recipe;

    private Double caloriesPerServing;
    private Double proteinGrams;
    private Double fatGrams;
    private Double carbohydrateGrams;
    private Double fiberGrams;
    private Double sugarGrams;
    private Double sodiumMg;
}
