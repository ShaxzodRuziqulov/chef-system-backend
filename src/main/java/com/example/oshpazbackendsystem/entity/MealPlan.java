package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import com.example.oshpazbackendsystem.entity.enums.PlanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "meal_plans",
    indexes = {
        @Index(name = "idx_meal_plans_user_id",    columnList = "user_id"),
        @Index(name = "idx_meal_plans_week_start", columnList = "week_start_date"),
        @Index(name = "idx_meal_plans_status",     columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(nullable = false)
    private LocalDate weekStartDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate weekEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PlanStatus status = PlanStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Version
    private Long version;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MealPlanEntry> entries = new ArrayList<>();

    @OneToOne(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShoppingList shoppingList;
}
