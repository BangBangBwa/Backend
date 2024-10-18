package com.bangbangbwa.backend.global.error.exception;

import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class ForbiddenException extends AccessDeniedException {

  private final String code;

  public ForbiddenException() {
    super(ApiErrorType.FORBIDDEN.getMessage());
    this.code = ApiErrorType.FORBIDDEN.name();
  }

  public ForbiddenException(final String message) {
    super(message);
    this.code = ApiErrorType.FORBIDDEN.name();
  }

  public ForbiddenException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
