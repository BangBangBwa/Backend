package com.bangbangbwa.backend.domain.auth.common.exception;

import com.bangbangbwa.backend.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class NotSupportedSnsTypeException extends BusinessException {

  private final String code;

  public NotSupportedSnsTypeException() {
    super(AuthErrorType.NOT_SUPPORTED_SNS_TYPE.getMessage());
    this.code = AuthErrorType.NOT_SUPPORTED_SNS_TYPE.name();
  }

  public NotSupportedSnsTypeException(final String message) {
    super(message);
    this.code = AuthErrorType.NOT_SUPPORTED_SNS_TYPE.name();
  }
}
