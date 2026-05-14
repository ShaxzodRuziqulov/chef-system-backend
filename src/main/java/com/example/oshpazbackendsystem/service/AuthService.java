package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.LoginRequest;
import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.response.AuthResponse;

public interface AuthService {

    /** Yangi foydalanuvchini ro'yxatdan o'tkazish va JWT qaytarish */
    AuthResponse register(RegisterRequest request);

    /** Email + parol orqali tizimga kirish */
    AuthResponse login(LoginRequest request);

    /** Refresh token yordamida yangi access token olish */
    AuthResponse refreshToken(String refreshToken);
}
