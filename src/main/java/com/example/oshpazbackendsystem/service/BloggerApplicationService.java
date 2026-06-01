package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.BloggerApplicationReviewRequest;
import com.example.oshpazbackendsystem.dto.response.BloggerApplicationDto;
import com.example.oshpazbackendsystem.entity.BloggerApplication;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.BloggerApplicationStatus;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.exception.BadRequestException;
import com.example.oshpazbackendsystem.exception.NotFoundException;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.BloggerApplicationRepository;
import com.example.oshpazbackendsystem.repository.UserRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BloggerApplicationService {

    private final BloggerApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    // Foydalanuvchi ariza yuboradi (hech qanday qo'shimcha ma'lumot shart emas)
    @Transactional
    public BloggerApplicationDto apply() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() == Role.BLOGGER || user.getRole() == Role.ADMIN) {
            throw new BadRequestException("ALREADY_BLOGGER", "Siz allaqachon oshpaz yoki adminsiz");
        }

        if (applicationRepository.existsByUserIdAndStatus(user.getId(), BloggerApplicationStatus.PENDING)) {
            throw new BadRequestException("APPLICATION_PENDING", "Sizning arizangiz ko'rib chiqilmoqda, kuting");
        }

        BloggerApplication application = BloggerApplication.builder()
                .user(user)
                .status(BloggerApplicationStatus.PENDING)
                .build();

        return toDto(applicationRepository.save(application));
    }

    // Foydalanuvchi o'z arizasining holatini ko'radi
    public BloggerApplicationDto getMyApplication() {
        User user = currentUserService.getCurrentUser();
        BloggerApplication app = applicationRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new NotFoundException("APPLICATION_NOT_FOUND", "Ariza topilmadi"));
        return toDto(app);
    }

    // Admin: kutayotgan arizalar ro'yxati
    public Page<BloggerApplicationDto> getPending(Pageable pageable) {
        return applicationRepository
                .findByStatus(BloggerApplicationStatus.PENDING, pageable)
                .map(this::toDto);
    }

    // Admin: barcha arizalar
    public Page<BloggerApplicationDto> getAll(Pageable pageable) {
        return applicationRepository.findAll(pageable).map(this::toDto);
    }

    // Admin: arizani tasdiqlash yoki rad etish
    @Transactional
    public BloggerApplicationDto review(Long applicationId, BloggerApplicationReviewRequest request) {
        BloggerApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("APPLICATION_NOT_FOUND", "Ariza topilmadi: " + applicationId));

        if (app.getStatus() != BloggerApplicationStatus.PENDING) {
            throw new BadRequestException("ALREADY_REVIEWED", "Bu ariza allaqachon ko'rib chiqilgan");
        }

        User admin = currentUserService.getCurrentUser();

        if (request.getApprove()) {
            app.setStatus(BloggerApplicationStatus.APPROVED);
            User user = app.getUser();
            user.setRole(Role.BLOGGER);
            userRepository.save(user);
        } else {
            app.setStatus(BloggerApplicationStatus.REJECTED);
        }

        app.setAdminNote(request.getAdminNote());
        app.setReviewedAt(LocalDateTime.now());
        app.setReviewedBy(admin);

        return toDto(applicationRepository.save(app));
    }

    private BloggerApplicationDto toDto(BloggerApplication app) {
        return BloggerApplicationDto.builder()
                .id(app.getId())
                .user(userMapper.toDto(app.getUser()))
                .status(app.getStatus())
                .adminNote(app.getAdminNote())
                .createdAt(app.getCreatedAt())
                .reviewedAt(app.getReviewedAt())
                .reviewedBy(app.getReviewedBy() != null ? userMapper.toDto(app.getReviewedBy()) : null)
                .build();
    }
}
