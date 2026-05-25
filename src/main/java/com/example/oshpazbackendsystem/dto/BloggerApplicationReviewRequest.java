package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloggerApplicationReviewRequest {

    @NotNull(message = "approve maydoni bo'sh bo'lmasligi kerak")
    private Boolean approve;

    @Size(max = 1000)
    private String adminNote;
}
