package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.response.UserResponse;
import com.example.oshpazbackendsystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // ─── Entity → UserResponse ────────────────────────────────────────────────
    UserResponse toResponse(User user);

    // ─── RegisterRequest → Entity ─────────────────────────────────────────────
    // DIQQAT: password service da hash qilinadi, bu yerda ignore
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "password",      ignore = true)
    @Mapping(target = "role",          ignore = true)
    @Mapping(target = "active",        ignore = true)
    @Mapping(target = "avatarUrl",     ignore = true)
    @Mapping(target = "recipes",       ignore = true)
    @Mapping(target = "mealPlans",     ignore = true)
    @Mapping(target = "shoppingLists", ignore = true)
    User toEntity(RegisterRequest request);
}
