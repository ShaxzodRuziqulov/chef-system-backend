package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDto create(UserDto response) {
        User user = mapper.toEntity(response);
        user = repository.save(user);
        return mapper.toDto(user);
    }

    public UserDto update(UserDto response) {
        User user = mapper.toEntity(response);
        user = repository.save(user);
        return mapper.toDto(user);
    }

    public UserDto findById(UUID id) {
        return mapper.toDto(findByUserId(id));
    }

    public UserDto deleteById(UUID id) {
        User user = findByUserId(id);
        user.setActive(false);
        return mapper.toDto(repository.save(user));
    }

    public User findByUserId(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("user not found"));
    }
}
