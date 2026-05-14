package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.MealPlanResponse;
import com.example.oshpazbackendsystem.entity.MealPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealPlanMapper extends EntityMapper<MealPlanResponse, MealPlan> {
}
