package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.dto.response.RecipeDto;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import com.example.oshpazbackendsystem.repository.UserRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final UserRepository     userRepository;
    private final RecipeRepository   recipeRepository;
    private final CurrentUserService currentUserService;
    private final RecipeService      recipeService;

    // ── Toggle: qo'shish yoki o'chirish ──────────────────────────────────────

    public boolean toggle(Long recipeId) {
        UUID userId = currentUserService.getCurrentUserId();

        if (!recipeRepository.existsById(recipeId)) {
            throw new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + recipeId);
        }

        boolean wasFavorited = userRepository.isFavorited(userId, recipeId);
        if (wasFavorited) {
            userRepository.removeFavorite(userId, recipeId);
        } else {
            userRepository.addFavorite(userId, recipeId);
        }
        return !wasFavorited;  // yangi holat: true = saqlandi, false = o'chirildi
    }

    // ── Saqlangan retseptlar ID lari (ko'rsatish uchun: ♥ belgisi) ─────────

    @Transactional(readOnly = true)
    public Set<Long> getFavoriteIds() {
        UUID userId = currentUserService.getCurrentUserId();
        return userRepository.findFavoriteIdsByUserId(userId);
    }

    // ── Saqlangan retseptlar ro'yxati (sahifalangan) ─────────────────────────

    @Transactional(readOnly = true)
    public Page<RecipeDto> getFavorites(Pageable pageable) {
        UUID userId = currentUserService.getCurrentUserId();
        return recipeRepository.findFavoritesByUserId(userId, pageable)
                .map(recipeService::toDto);
    }
}
