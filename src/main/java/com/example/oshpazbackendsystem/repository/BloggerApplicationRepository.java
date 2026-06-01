package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.BloggerApplication;
import com.example.oshpazbackendsystem.entity.enums.BloggerApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BloggerApplicationRepository extends JpaRepository<BloggerApplication, Long> {

    // Foydalanuvchining oxirgi arizasini topish
    Optional<BloggerApplication> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    // Kutayotgan arizalar (admin uchun)
    Page<BloggerApplication> findByStatus(BloggerApplicationStatus status, Pageable pageable);

    // Foydalanuvchida PENDING ariza bormi
    boolean existsByUserIdAndStatus(UUID userId, BloggerApplicationStatus status);

    // Foydalanuvchining oxirgi APPROVED arizasini topish (leaveBlogger uchun)
    Optional<BloggerApplication> findTopByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, BloggerApplicationStatus status);
}
