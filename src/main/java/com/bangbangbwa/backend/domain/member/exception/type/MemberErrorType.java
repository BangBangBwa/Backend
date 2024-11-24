package com.bangbangbwa.backend.domain.member.exception.type;

import lombok.Getter;

@Getter
public enum MemberErrorType {
  NOT_EXISTS_REMAIN_NICKNAME("닉네임을 직접 입력 바랍니다."),
  NEGATIVE_NICKNAME_COUNT("음수를 사용할 수 없습니다."),
  MAX_NICKNAME_COUNT_EXCEEDED("닉네임 추천 가능 갯수를 초과했습니다."),
  INVALID_SNS_TYPE_ERROR("지원하지 않는 SNS 타입입니다."),
  INVALID_INTEREST_ERROR("지원하지 않는 관심 태그입니다."),
  NOT_FOUND_MEMBER("존재하지 않는 회원입니다."),
  NOT_SIGN_UP_MEMBER("가입되지 않은 회원입니다."),
  DUPLICATED_NICKNAME_ERROR("이미 사용중인 닉네임 입니다."),
  EMPTY_NICKNAME("닉네임이 공백 혹은 null 입니다."),

  UN_AUTHENTICATION_MEMBER_EXCEPTION("인증되지 않은 사용자입니다."),
  AUTHENTICATION_NULL_ERROR("Authentication 이 NULL 입니다."),
  AUTHENTICATION_NAME_NULL_ERROR("Authentication name 이 NULL 입니다."),
  NOT_PARSED_VALUE_ERROR("파싱되지 않는 값입니다.");


  private final String message;

  MemberErrorType(String message) {
    this.message = message;
  }
}
