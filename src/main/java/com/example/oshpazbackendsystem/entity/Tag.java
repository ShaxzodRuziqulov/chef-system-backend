package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
    name = "tags",
    indexes = {
        @Index(name = "idx_tags_name_uz", columnList = "name_uz", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String nameUz;

    @Size(max = 50)
    @Column(length = 50)
    private String nameRu;

    @Size(max = 50)
    @Column(length = 50)
    private String nameEng;

    @Column(length = 255)
    private String description;
}
