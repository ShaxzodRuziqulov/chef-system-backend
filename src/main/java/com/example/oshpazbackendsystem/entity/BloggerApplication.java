package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import com.example.oshpazbackendsystem.entity.enums.BloggerApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blogger_applications", indexes = {
    @Index(name = "idx_blogger_app_user",   columnList = "user_id"),
    @Index(name = "idx_blogger_app_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloggerApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Foydalanuvchi yozgan motivatsiya matni
    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivation;

    // Instagram, YouTube, blog URL — ixtiyoriy
    @Column(length = 500)
    private String socialLinks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BloggerApplicationStatus status = BloggerApplicationStatus.PENDING;

    // Admin rad etganda sabab ko'rsatishi uchun
    @Column(columnDefinition = "TEXT")
    private String adminNote;
}
