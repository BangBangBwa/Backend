package com.bangbangbwa.backend.domain.members.common.exception;

import lombok.Getter;

@Getter
public enum MemberErrorType {

  OAUTH2_FEIGN_ERROR("OAuth2 리소스 서버와 통신을 실패했습니다."),

  URI_SYNTAX_ERROR("URI 형식이 잘못되었습니다.");

  private final String message;

  MemberErrorType(String message) {
    this.message = message;
  }
}
