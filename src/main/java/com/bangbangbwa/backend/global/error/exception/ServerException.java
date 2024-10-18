package com.bangbangbwa.backend.global.error.exception;


import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

  private final String code;

  public ServerException() {
    super(ApiErrorType.INTERNAL_SERVER_ERROR.getMessage());
    this.code = ApiErrorType.INTERNAL_SERVER_ERROR.name();
  }

  public ServerException(final String message) {
    super(message);
    this.code = ApiErrorType.INTERNAL_SERVER_ERROR.name();
  }

  public ServerException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
