package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, User> {
}
