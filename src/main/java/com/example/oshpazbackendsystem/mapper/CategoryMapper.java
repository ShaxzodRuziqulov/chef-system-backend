package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.CategoryDto;
import com.example.oshpazbackendsystem.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDto, Category> {
}
