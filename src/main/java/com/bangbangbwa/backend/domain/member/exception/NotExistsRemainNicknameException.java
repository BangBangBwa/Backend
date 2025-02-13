package com.bangbangbwa.backend.domain.member.exception;

import com.bangbangbwa.backend.domain.member.exception.type.MemberErrorType;
import com.bangbangbwa.backend.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class NotExistsRemainNicknameException extends BusinessException {

  private final String code;

  public NotExistsRemainNicknameException() {
    this(MemberErrorType.NOT_EXISTS_REMAIN_NICKNAME.getMessage());
  }

  public NotExistsRemainNicknameException(final String message) {
    super(message);
    this.code = MemberErrorType.NOT_EXISTS_REMAIN_NICKNAME.name();
  }
}