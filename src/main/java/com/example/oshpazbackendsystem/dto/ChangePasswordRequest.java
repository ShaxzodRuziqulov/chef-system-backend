package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Joriy parol kiritilishi shart")
    private String currentPassword;

    @NotBlank(message = "Yangi parol kiritilishi shart")
    @Size(min = 6, max = 100, message = "Yangi parol kamida 6 ta belgidan iborat bo'lishi kerak")
    private String newPassword;
}
