package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_categories_name_uz", columnList = "name_uz", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
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
    private String iconUrl;

    @Size(max = 10)
    @Column(length = 10)
    private String colorCode;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Recipe> recipes = new ArrayList<>();
}
