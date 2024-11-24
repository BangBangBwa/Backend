package com.bangbangbwa.backend.domain.member.exception;

import com.bangbangbwa.backend.domain.member.exception.type.MemberErrorType;
import com.bangbangbwa.backend.global.error.exception.ServerException;
import lombok.Getter;

@Getter
public class UnAuthenticationMemberException extends ServerException {

  private final String code;

  public UnAuthenticationMemberException() {
    this(MemberErrorType.UN_AUTHENTICATION_MEMBER_EXCEPTION.getMessage());
  }

  public UnAuthenticationMemberException(String message) {
    super(message);
    this.code = MemberErrorType.UN_AUTHENTICATION_MEMBER_EXCEPTION.name();
  }
}
