package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    /** Access token amal qilish muddati (millisekund) */
    private long expiresIn;

    /** Tizimga kirgan foydalanuvchi ma'lumotlari */
    private UserDto user;
}
