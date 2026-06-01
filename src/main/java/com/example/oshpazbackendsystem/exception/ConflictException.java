package com.example.oshpazbackendsystem.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConflictException extends RuntimeException {
    private final String code;
    private final Object details;

    public ConflictException(String code, String message) {
        this(code, message, null);
    }

    public ConflictException(String code, String message, Object details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}
