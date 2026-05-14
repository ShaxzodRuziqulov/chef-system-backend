package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import com.example.oshpazbackendsystem.entity.enums.ShoppingItemStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "shopping_list_items",
    indexes = {
        @Index(name = "idx_shopping_list_items_list_id",       columnList = "shopping_list_id"),
        @Index(name = "idx_shopping_list_items_ingredient_id", columnList = "ingredient_id"),
        @Index(name = "idx_shopping_list_items_status",        columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ShoppingItemStatus status = ShoppingItemStatus.PENDING;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(length = 255)
    private String notes;

    @Column(length = 100)
    private String grocerySection;
}
