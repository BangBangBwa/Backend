package com.bangbangbwa.backend.domain.member.exception;

import com.bangbangbwa.backend.domain.member.exception.type.MemberErrorType;
import com.bangbangbwa.backend.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class EmptyNicknameException extends BusinessException {

  private final String code;

  public EmptyNicknameException() {
    this(MemberErrorType.EMPTY_NICKNAME.getMessage());
  }

  public EmptyNicknameException(String message) {
    super(message);
    this.code = MemberErrorType.EMPTY_NICKNAME.name();
  }
}
