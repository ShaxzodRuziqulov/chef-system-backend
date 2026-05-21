package com.example.oshpazbackendsystem.dto;

import com.example.oshpazbackendsystem.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserUpdateRequest {

    @Size(max = 100)
    private String fullName;

    @Size(min = 3, max = 50)
    private String username;

    @Email
    @Size(max = 100)
    private String email;

    private Role role;

    private Boolean active;

    /** Agar to'ldirilsa — yangi parol o'rnatiladi (joriy parolsiz) */
    @Size(min = 4, max = 100, message = "Parol kamida 4 ta belgidan iborat bo'lishi kerak")
    private String newPassword;
}
