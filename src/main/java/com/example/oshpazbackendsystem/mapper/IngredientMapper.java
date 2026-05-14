package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.IngredientDto;
import com.example.oshpazbackendsystem.entity.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper extends EntityMapper<IngredientDto, Ingredient> {
}
