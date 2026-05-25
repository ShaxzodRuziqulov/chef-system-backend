package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.response.PlatformFeaturesDto;
import com.example.oshpazbackendsystem.dto.response.PlatformStatsDto;
import com.example.oshpazbackendsystem.repository.CategoryRepository;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public PlatformStatsDto getStats() {
        long totalRecipes = recipeRepository.countByVisibleTrueAndDeletedFalse();
        long totalUsers = userRepository.countByActiveTrue();
        long totalCategories = categoryRepository.count();

        List<PlatformStatsDto.TopRecipeDto> topRecipes = recipeRepository
                .findTop5ByVisibleTrueAndDeletedFalseOrderByViewCountDesc()
                .stream()
                .map(r -> PlatformStatsDto.TopRecipeDto.builder()
                        .id(r.getId())
                        .titleUz(r.getTitleUz())
                        .titleRu(r.getTitleRu())
                        .titleEng(r.getTitleEng())
                        .imageUrl(r.getImageUrl())
                        .viewCount(r.getViewCount())
                        .averageRating(r.getAverageRating())
                        .build())
                .toList();

        return PlatformStatsDto.builder()
                .totalRecipes(totalRecipes)
                .totalUsers(totalUsers)
                .totalCategories(totalCategories)
                .topRecipes(topRecipes)
                .build();
    }

    public PlatformFeaturesDto getFeatures() {
        return PlatformFeaturesDto.builder()
                .publicFeatures(List.of(
                        "Retseptlarni ko'rish va qidirish",
                        "Kategoriyalar bo'yicha filtrlash",
                        "Qiyinlik darajasi bo'yicha saralash",
                        "Retsept tarkibi va pishirish bosqichlarini o'rganish",
                        "Izohlarni o'qish",
                        "O'rtacha reytingni ko'rish"
                ))
                .memberFeatures(List.of(
                        "Retseptlarga izoh qoldirish",
                        "Retseptlarga reyting berish (1-5 yulduz)",
                        "Sevimli retseptlar ro'yxatini yuritish",
                        "Haftalik ovqat rejasini tuzish",
                        "Ovqat rejasidan xarid ro'yxatini avtomatik yaratish",
                        "Profil sozlamalari va parol o'zgartirish"
                ))
                .bloggerFeatures(List.of(
                        "Yangi retsept yaratish va nashr qilish",
                        "Rasm va video yuklash",
                        "Ingredientlar va pishirish bosqichlarini boshqarish",
                        "O'z retseptlarini tahrirlash va o'chirish"
                ))
                .build();
    }
}
