package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthUserResponse {
    private UUID id;
    private String fullName;
    private Role roles;
    @JsonProperty("avatar_url")
    private String avatarUrl;
}
