package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Uzbekcha nomi bo'yicha topish
    Optional<Tag> findByNameUzIgnoreCase(String nameUz);

    // Teg mavjudligini tekshirish
    boolean existsByNameUz(String nameUz);
}