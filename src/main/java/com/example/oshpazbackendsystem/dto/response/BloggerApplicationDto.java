package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.BloggerApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BloggerApplicationDto {
    private Long id;
    private UserDto user;
    private BloggerApplicationStatus status;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private UserDto reviewedBy;
}
