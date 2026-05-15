package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.LoginRequest;
import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.UpdateProfileRequest;
import com.example.oshpazbackendsystem.dto.response.AuthResponse;
import com.example.oshpazbackendsystem.exeption.ConflictException;
import com.example.oshpazbackendsystem.dto.response.AuthTokenResponse;
import com.example.oshpazbackendsystem.dto.response.AuthUserResponse;
import com.example.oshpazbackendsystem.dto.response.RefreshTokenRequestDto;
import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("EMAIL_EXISTS", "Bu email allaqachon ro'yxatdan o'tgan: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("USERNAME_EXISTS", "Bu username band: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .active(true)
                .build();

        userRepository.save(user);

        UserDto userDto = userMapper.toDto(user);
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .expiresIn(jwtService.getExpirationTime())
                .user(userDto)
                .build();
    }

    public User login(LoginRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        return userRepository.findWithRolesByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
    }

    public User refresh(RefreshTokenRequestDto dto) {
        validateRefreshToken(dto);

        String username = jwtService.extractUsername(dto.getRefreshToken());
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!jwtService.isTokenValid(dto.getRefreshToken(), user)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return user;
    }

    public void validateRefreshToken(RefreshTokenRequestDto dto) {
        if (dto.getRefreshToken() == null || dto.getRefreshToken().isBlank()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        if (!jwtService.isRefreshToken(dto.getRefreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    public AuthTokenResponse buildAuthTokenResponse(User authUser) {
        AuthTokenResponse response = new AuthTokenResponse();
        response.setAccessToken(jwtService.generateToken(authUser));
        response.setRefreshToken(jwtService.generateRefreshToken(authUser));
        response.setUser(toAuthUserResponse(authUser));
        return response;
    }

    public AuthUserResponse updateProfile(User user, UpdateProfileRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        userRepository.save(user);
        return toAuthUserResponse(user);
    }

    public AuthUserResponse toAuthUserResponse(User user) {
        AuthUserResponse response = new AuthUserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRoles(user.getRole());
        return response;
    }
}
