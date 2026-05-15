package com.example.oshpazbackendsystem.exeption;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
  private final String code;

  public BadRequestException(String message) {
    this("BAD_REQUEST", message);
  }

  public BadRequestException(String code, String message) {
    super(message);
    this.code = code;
  }
}
