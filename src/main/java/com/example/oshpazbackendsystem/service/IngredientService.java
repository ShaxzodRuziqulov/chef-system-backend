package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.IngredientRequest;
import com.example.oshpazbackendsystem.dto.response.IngredientDto;
import com.example.oshpazbackendsystem.entity.Ingredient;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.mapper.IngredientMapper;
import com.example.oshpazbackendsystem.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository repository;
    private final IngredientMapper mapper;

    @Transactional(readOnly = true)
    public Page<IngredientDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<IngredientDto> search(String keyword, Pageable pageable) {
        return repository.searchByName(keyword, pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public IngredientDto findById(Long id) {
        return mapper.toDto(getById(id));
    }

    public IngredientDto create(IngredientRequest request) {
        Ingredient ingredient = Ingredient.builder()
                .nameUz(request.getNameUz())
                .nameRu(request.getNameRu())
                .nameEng(request.getNameEng())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .defaultUnit(request.getDefaultUnit())
                .caloriesPer100g(request.getCaloriesPer100g())
                .allergen(request.isAllergen())
                .build();
        return mapper.toDto(repository.save(ingredient));
    }

    public IngredientDto update(Long id, IngredientRequest request) {
        Ingredient ingredient = getById(id);
        ingredient.setNameUz(request.getNameUz());
        ingredient.setNameRu(request.getNameRu());
        ingredient.setNameEng(request.getNameEng());
        ingredient.setDescription(request.getDescription());
        ingredient.setImageUrl(request.getImageUrl());
        ingredient.setDefaultUnit(request.getDefaultUnit());
        ingredient.setCaloriesPer100g(request.getCaloriesPer100g());
        ingredient.setAllergen(request.isAllergen());
        return mapper.toDto(repository.save(ingredient));
    }

    public void delete(Long id) {
        repository.delete(getById(id));
    }

    // Boshqa service lar uchun
    public Ingredient getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("INGREDIENT_NOT_FOUND", "Ingredient topilmadi: " + id));
    }
}
