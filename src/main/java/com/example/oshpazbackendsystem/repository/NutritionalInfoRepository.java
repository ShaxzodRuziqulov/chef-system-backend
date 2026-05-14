package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.NutritionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutritionalInfoRepository extends JpaRepository<NutritionalInfo, Long> {

    // Retsept ID bo'yicha kaloriya ma'lumotini topish
    Optional<NutritionalInfo> findByRecipeId(Long recipeId);
}
