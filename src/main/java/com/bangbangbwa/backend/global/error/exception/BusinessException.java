package com.bangbangbwa.backend.global.error.exception;


import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final String code;

  public BusinessException() {
    super(ApiErrorType.BAD_REQUEST.getMessage());
    this.code = ApiErrorType.BAD_REQUEST.name();
  }

  public BusinessException(final String message) {
    super(message);
    this.code = ApiErrorType.BAD_REQUEST.name();
  }

  public BusinessException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
