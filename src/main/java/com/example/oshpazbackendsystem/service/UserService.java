package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.exeption.NotFoundException;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        return mapper.toDto(users);
    }

    public UserDto findById(UUID id) {
        return mapper.toDto(findByUserId(id));
    }

    public UserDto deleteById(UUID id) {
        User user = findByUserId(id);
        user.setActive(false);
        return mapper.toDto(repository.save(user));
    }

    public UserDto activateById(UUID id) {
        User user = findByUserId(id);
        user.setActive(true);
        return mapper.toDto(repository.save(user));
    }

    public User findByUserId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "Foydalanuvchi topilmadi: " + id));
    }
}
