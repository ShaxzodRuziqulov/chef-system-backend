package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100, message = "Ism 100 ta belgidan oshmasligi kerak")
    private String fullName;

    @Size(max = 500, message = "Avatar URL 500 ta belgidan oshmasligi kerak")
    private String avatarUrl;
}
