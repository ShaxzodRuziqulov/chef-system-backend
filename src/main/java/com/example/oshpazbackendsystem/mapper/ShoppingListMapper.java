package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.ShoppingListDto;
import com.example.oshpazbackendsystem.entity.ShoppingList;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShoppingListMapper extends EntityMapper<ShoppingListDto, ShoppingList> {
}
