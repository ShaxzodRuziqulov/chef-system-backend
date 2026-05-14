package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Uzbekcha nomi bo'yicha topish
    Optional<Category> findByNameUzIgnoreCase(String nameUz);

    // Takror qo'shmaslik uchun tekshirish
    boolean existsByNameUz(String nameUz);
}