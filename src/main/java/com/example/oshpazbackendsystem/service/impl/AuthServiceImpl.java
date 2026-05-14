package com.example.oshpazbackendsystem.service.impl;

import com.example.oshpazbackendsystem.dto.LoginRequest;
import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.response.AuthResponse;
import com.example.oshpazbackendsystem.dto.response.UserDto;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.mapper.UserMapper;
import com.example.oshpazbackendsystem.repository.UserRepository;
import com.example.oshpazbackendsystem.service.AuthService;
import com.example.oshpazbackendsystem.service.JwtService;
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
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {

        // Username yoki email band ekanligini tekshirish
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Bu email allaqachon ro'yxatdan o'tgan: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Bu username band: " + request.getUsername());
        }

        // Yangi foydalanuvchi yaratish
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .active(true)
                .build();

        userRepository.save(user);

        // Token generatsiya qilish
        String accessToken  = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Email orqali userni topamiz (LoginRequest emaildan foydalanadi)
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email yoki parol noto'g'ri"));

        // Parolni Spring Security orqali tekshiramiz (username ishlatiladi)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        String accessToken  = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Berilgan token refresh token emas");
        }
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh token muddati o'tgan, qaytadan kiring");
        }

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Foydalanuvchi topilmadi"));

        String newAccessToken  = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(newAccessToken, newRefreshToken, user);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        UserDto userDto = userMapper.toDto(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userDto)
                .build();
    }
}
