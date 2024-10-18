package com.bangbangbwa.backend.global.error.exception;

import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

  private final String code;

  public ServerException() {
    this(ApiErrorType.INTERNAL_SERVER_ERROR.getMessage());
  }

  public ServerException(final String message) {
    this(message, ApiErrorType.INTERNAL_SERVER_ERROR.name());
  }

  public ServerException(final String message, final String code) {
    super(message);
    this.code = code;
  }
}
