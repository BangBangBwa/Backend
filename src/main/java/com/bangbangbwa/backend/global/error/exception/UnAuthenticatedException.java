package com.bangbangbwa.backend.global.error.exception;

import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import lombok.Getter;

@Getter
public class UnAuthenticatedException extends RuntimeException {

  private final String code;

  public UnAuthenticatedException() {
    super(ApiErrorType.UNAUTHENTICATED.getMessage());
    this.code = ApiErrorType.UNAUTHENTICATED.name();
  }

  public UnAuthenticatedException(final String message) {
    super(message);
    this.code = ApiErrorType.UNAUTHENTICATED.name();
  }

  public UnAuthenticatedException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
