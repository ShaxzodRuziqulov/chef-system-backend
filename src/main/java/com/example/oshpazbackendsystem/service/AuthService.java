package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.ChangePasswordRequest;
import com.example.oshpazbackendsystem.dto.LoginRequest;
import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.UpdateProfileRequest;
import com.example.oshpazbackendsystem.dto.response.AuthResponse;
import com.example.oshpazbackendsystem.exception.ConflictException;
import com.example.oshpazbackendsystem.exception.BadRequestException;
import com.example.oshpazbackendsystem.dto.response.AuthTokenResponse;
import com.example.oshpazbackendsystem.dto.response.AuthUserResponse;
import com.example.oshpazbackendsystem.dto.response.RefreshTokenRequestDto;
import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
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
        // Email berilmagan bo'lsa — username asosida avtomatik generatsiya
        String email = (request.getEmail() != null && !request.getEmail().isBlank())
                ? request.getEmail().trim().toLowerCase()
                : request.getUsername().toLowerCase() + "@oshpaz.local";

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("EMAIL_EXISTS", "Bu email allaqachon ro'yxatdan o'tgan");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("USERNAME_EXISTS", "Bu username band: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(email)
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

    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Joriy parol noto'g'ri");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Username orqali parolni to'g'ridan-to'g'ri tiklash (emailsiz).
     */
    public void resetByUsername(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND", "Foydalanuvchi topilmadi"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Token orqali parol tiklash — ResetPasswordPage dan chaqiriladi.
     * Token DB da saqlangan va muddati tekshiriladi.
     */
    public void resetByToken(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("INVALID_TOKEN", "Token noto'g'ri yoki muddati o'tgan"));

        if (user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("TOKEN_EXPIRED", "Token muddati o'tgan. Iltimos, qaytadan so'rang.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public AuthUserResponse toAuthUserResponse(User user) {
        AuthUserResponse response = new AuthUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRoles(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
