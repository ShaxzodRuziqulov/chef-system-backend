package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.RecipeDto;
import com.example.oshpazbackendsystem.entity.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeMapper extends EntityMapper<RecipeDto, Recipe> {

}
