package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.IngredientRequest;
import com.example.oshpazbackendsystem.dto.response.IngredientResponse;
import com.example.oshpazbackendsystem.entity.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface IngredientMapper {

    @Mapping(target = "id", ignore = true)
    Ingredient toEntity(IngredientRequest request);

    IngredientResponse toResponse(Ingredient ingredient);

    @Mapping(target = "id", ignore = true)
    void updateEntity(IngredientRequest request, @MappingTarget Ingredient ingredient);
}
