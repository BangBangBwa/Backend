package com.bangbangbwa.backend.global.error.exception;

import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final String code;

  public BusinessException() {
    this(ApiErrorType.BAD_REQUEST.getMessage());
  }

  public BusinessException(final String message) {
    this(message, ApiErrorType.BAD_REQUEST.name());
  }

  public BusinessException(final String message, final String code) {
    super(message);
    this.code = code;
  }
}
