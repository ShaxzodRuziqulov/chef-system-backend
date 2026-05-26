package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.AdminUserUpdateRequest;
import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.exeption.BadRequestException;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository    repository;
    private final UserMapper        mapper;
    private final PasswordEncoder   passwordEncoder;
    private final com.example.oshpazbackendsystem.service.security.CurrentUserService currentUserService;

    public UserDto create(UserDto response) {
        User user = mapper.toEntity(response);
        user = repository.save(user);
        return mapper.toDto(user);
    }

    public UserDto update(UserDto response) {
        User user = mapper.toEntity(response);
        user = repository.save(user);
        return mapper.toDto(user);
    }

    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        return mapper.toDto(users);
    }

    /** Admin: sahifalangan ro'yxat, ixtiyoriy qidiruv */
    public Page<UserDto> findAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = (search != null && !search.isBlank())
                ? repository.searchByKeyword(search.trim(), pageable)
                : repository.findAll(pageable);
        return users.map(mapper::toDto);
    }

    /** Faol foydalanuvchilar soni */
    public long countActive() {
        return repository.countByActiveTrue();
    }

    /** Admin tomonidan foydalanuvchi parolini yangilash (joriy parolsiz) */
    public void resetPasswordByAdmin(UUID id, String newPassword) {
        User user = findByUserId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    /** Admin tomonidan foydalanuvchi ma'lumotlarini yangilash */
    public UserDto updateByAdmin(UUID id, AdminUserUpdateRequest req) {
        User user = findByUserId(id);
        if (req.getFullName() != null)  user.setFullName(req.getFullName());
        if (req.getUsername() != null)  user.setUsername(req.getUsername());
        if (req.getEmail()    != null)  user.setEmail(req.getEmail());
        if (req.getRole()     != null)  user.setRole(req.getRole());
        if (req.getActive()   != null)  user.setActive(req.getActive());
        return mapper.toDto(repository.save(user));
    }

    public UserDto findById(UUID id) {
        return mapper.toDto(findByUserId(id));
    }

    public UserDto deleteById(UUID id) {
        User user = findByUserId(id);
        user.setActive(false);
        return mapper.toDto(repository.save(user));
    }

    public UserDto activateById(UUID id) {
        User user = findByUserId(id);
        user.setActive(true);
        return mapper.toDto(repository.save(user));
    }

    public User findByUserId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "Foydalanuvchi topilmadi: " + id));
    }

    @Transactional
    public UserDto becomeBlogger(Boolean termsAccepted) {
        if (!Boolean.TRUE.equals(termsAccepted)) {
            throw new BadRequestException("TERMS_NOT_ACCEPTED", "Foydalanish shartlarini qabul qilishingiz kerak");
        }
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.USER) {
            throw new BadRequestException("ALREADY_UPGRADED", "Siz allaqachon " + user.getRole() + " rolidasiz");
        }
        user.setRole(Role.BLOGGER);
        return mapper.toDto(repository.save(user));
    }

    @Transactional
    public UserDto leaveBlogger() {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.BLOGGER) {
            throw new BadRequestException("NOT_BLOGGER", "Siz oshpaz rolidа emassiz");
        }
        user.setRole(Role.USER);
        return mapper.toDto(repository.save(user));
    }
}
