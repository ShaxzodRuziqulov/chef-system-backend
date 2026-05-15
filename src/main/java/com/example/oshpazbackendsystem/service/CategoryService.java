package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.CategoryRequest;
import com.example.oshpazbackendsystem.dto.response.CategoryDto;
import com.example.oshpazbackendsystem.entity.Category;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.mapper.CategoryMapper;
import com.example.oshpazbackendsystem.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return mapper.toDto(repository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        return mapper.toDto(getById(id));
    }

    public CategoryDto create(CategoryRequest request) {
        Category category = Category.builder()
                .nameUz(request.getNameUz())
                .nameRu(request.getNameRu())
                .nameEng(request.getNameEng())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .colorCode(request.getColorCode())
                .build();
        return mapper.toDto(repository.save(category));
    }

    public CategoryDto update(Long id, CategoryRequest request) {
        Category category = getById(id);
        category.setNameUz(request.getNameUz());
        category.setNameRu(request.getNameRu());
        category.setNameEng(request.getNameEng());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());
        category.setColorCode(request.getColorCode());
        return mapper.toDto(repository.save(category));
    }

    public void delete(Long id) {
        repository.delete(getById(id));
    }

    public Category getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND", "Kategoriya topilmadi: " + id));
    }
}
