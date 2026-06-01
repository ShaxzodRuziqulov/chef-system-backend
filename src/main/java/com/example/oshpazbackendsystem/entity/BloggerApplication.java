package com.example.oshpazbackendsystem.entity;

import com.example.oshpazbackendsystem.entity.base.BaseEntity;
import com.example.oshpazbackendsystem.entity.enums.BloggerApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BloggerApplicationStatus status = BloggerApplicationStatus.PENDING;

    // Admin rad etganda sabab ko'rsatishi uchun
    @Column(columnDefinition = "TEXT")
    private String adminNote;

    // Qachon ko'rib chiqildi
    @Column
    private LocalDateTime reviewedAt;

    // Qaysi admin ko'rib chiqdi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
}
