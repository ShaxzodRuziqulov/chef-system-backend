package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
    @Size(min = 3, max = 50, message = "Username 3-50 belgi orasida bo'lishi kerak")
    private String username;

    @NotBlank(message = "Email bo'sh bo'lmasligi kerak")
    @Email(message = "Email formati noto'g'ri")
    private String email;

    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
    @Size(min = 4, message = "Parol kamida 4 belgi bo'lishi kerak")
    private String password;

    @Size(max = 100)
    private String fullName;
}
