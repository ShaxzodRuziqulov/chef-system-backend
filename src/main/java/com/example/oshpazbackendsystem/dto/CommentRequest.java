package com.example.oshpazbackendsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Izoh bo'sh bo'lmasligi kerak")
    @Size(max = 2000, message = "Izoh 2000 belgidan oshmasligi kerak")
    private String content;
}
