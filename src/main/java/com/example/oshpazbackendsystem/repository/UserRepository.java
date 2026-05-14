package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Login uchun email bo'yicha topish
    Optional<User> findByEmail(String email);

    // Username bo'yicha topish
    Optional<User> findByUsername(String username);

    // Ro'yxatdan o'tishda email band emasligini tekshirish
    boolean existsByEmail(String email);

    // Ro'yxatdan o'tishda username band emasligini tekshirish
    boolean existsByUsername(String username);

    // Faqat faol foydalanuvchini email bo'yicha topish (login uchun)
    Optional<User> findByEmailAndActiveTrue(String email);
}