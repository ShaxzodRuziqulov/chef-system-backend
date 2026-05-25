package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloggerApplicationRequest {

    @NotBlank(message = "Motivatsiya matni bo'sh bo'lmasligi kerak")
    @Size(min = 50, max = 2000, message = "Motivatsiya 50-2000 belgi orasida bo'lishi kerak")
    private String motivation;

    @Size(max = 500, message = "Ijtimoiy tarmoq havolalari 500 belgidan oshmasligi kerak")
    private String socialLinks;
}
