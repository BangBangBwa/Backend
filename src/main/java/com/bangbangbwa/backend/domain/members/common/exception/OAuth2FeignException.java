package com.bangbangbwa.backend.domain.members.common.exception;

import com.bangbangbwa.backend.global.error.exception.ServerException;
import lombok.Getter;

@Getter
public class OAuth2FeignException extends ServerException {

  private final String code;

  public OAuth2FeignException() {
    super(MemberErrorType.OAUTH2_FEIGN_ERROR.getMessage());
    this.code = MemberErrorType.OAUTH2_FEIGN_ERROR.name();
  }

  public OAuth2FeignException(final String message) {
    super(message);
    this.code = MemberErrorType.OAUTH2_FEIGN_ERROR.name();
  }
}
