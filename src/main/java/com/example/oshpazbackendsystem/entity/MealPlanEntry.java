package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.enums.MealType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;

@Entity
@Table(
    name = "meal_plan_entries",
    indexes = {
        @Index(name = "idx_meal_plan_entries_plan_id",   columnList = "meal_plan_id"),
        @Index(name = "idx_meal_plan_entries_day",       columnList = "day_of_week"),
        @Index(name = "idx_meal_plan_entries_meal_type", columnList = "meal_type")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_meal_plan_day_type",
            columnNames = {"meal_plan_id", "day_of_week", "meal_type"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private MealType mealType;

    @Min(1)
    @Column(nullable = false)
    @Builder.Default
    private Integer servings = 1;

    @Column(length = 255)
    private String notes;
}
