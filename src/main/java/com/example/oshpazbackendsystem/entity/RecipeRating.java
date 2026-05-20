package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(
    name = "recipe_ratings",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_rating_user_recipe",
        columnNames = {"user_id", "recipe_id"}
    ),
    indexes = {
        @Index(name = "idx_rating_recipe_id", columnList = "recipe_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Min(1)
    @Max(5)
    @Column(nullable = false, columnDefinition = "smallint")
    private short score;
}
