package com.bangbangbwa.backend.domain.member.exception;

import com.bangbangbwa.backend.domain.member.exception.type.MemberErrorType;
import com.bangbangbwa.backend.global.error.exception.UnAuthenticatedException;
import lombok.Getter;

@Getter
public class NotSignupMemberException extends UnAuthenticatedException {

  private final String code;

  public NotSignupMemberException() {
    this(MemberErrorType.NOT_SIGN_UP_MEMBER.getMessage());
  }

  public NotSignupMemberException(final String message) {
    super(message);
    this.code = MemberErrorType.NOT_SIGN_UP_MEMBER.name();
  }
}
