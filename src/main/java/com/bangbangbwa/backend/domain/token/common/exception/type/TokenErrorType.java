package com.bangbangbwa.backend.domain.token.common.exception.type;

import lombok.Getter;

@Getter
public enum TokenErrorType {

  // JWT
  EXPIRED_TOKEN("만료된 토큰입니다."),
  INVALID_TOKEN("유효하지 않은 토큰입니다."),
  INVALID_JWT_SIGNATURE("유효하지 않은 서명입니다."),
  NOT_FOUND_TOKEN("토큰정보를 찾을 수 없습니다.");

  private final String message;

  TokenErrorType(String message) {
    this.message = message;
  }
}