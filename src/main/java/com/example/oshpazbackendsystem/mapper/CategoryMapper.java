package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.CategoryRequest;
import com.example.oshpazbackendsystem.dto.response.CategoryResponse;
import com.example.oshpazbackendsystem.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "recipes", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "recipes", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
