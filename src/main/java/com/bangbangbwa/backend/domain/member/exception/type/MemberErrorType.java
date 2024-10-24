package com.bangbangbwa.backend.domain.member.exception.type;

import lombok.Getter;

@Getter
public enum MemberErrorType {
  INVALID_SNS_TYPE_ERROR("지원하지 않는 SNS 타입입니다."),
  INVALID_INTEREST_ERROR("지원하지 않는 관심 태그입니다."),
  NOT_FOUND_MEMBER("존재하지 않는 회원입니다."),
  NOT_SIGN_UP_MEMBER("가입되지 않은 회원입니다.");

  private final String message;

  MemberErrorType(String message) {
    this.message = message;
  }
}
