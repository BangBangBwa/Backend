package com.bangbangbwa.backend.global.error.type;

import lombok.Getter;

@Getter
public enum ApiErrorType {

  BAD_REQUEST("잘못된 요청입니다."),
  UNAUTHENTICATED("인증이 실패하였습니다."),
  FORBIDDEN("권한이 없습니다."),
  INTERNAL_SERVER_ERROR("서버에서 에러가 발생하였습니다.");

  private final String message;

  ApiErrorType(String message) {
    this.message = message;
  }
}
