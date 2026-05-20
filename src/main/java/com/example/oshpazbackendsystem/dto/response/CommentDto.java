package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.RecipeComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CommentDto {

    private final Long          id;
    private final UUID          userId;
    private final String        userName;
    private final String        content;
    private final LocalDateTime createdAt;
    private final boolean       mine;     // current user's own comment

    public static CommentDto from(RecipeComment c, UUID currentUserId) {
        String name = c.getUser().getFullName() != null && !c.getUser().getFullName().isBlank()
                ? c.getUser().getFullName()
                : c.getUser().getUsername();
        return CommentDto.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .userName(name)
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .mine(currentUserId != null && currentUserId.equals(c.getUser().getId()))
                .build();
    }
}
