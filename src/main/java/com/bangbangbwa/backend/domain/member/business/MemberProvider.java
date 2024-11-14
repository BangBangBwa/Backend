package com.bangbangbwa.backend.domain.member.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.exception.AuthenticationNameNullException;
import com.bangbangbwa.backend.domain.member.exception.AuthenticationNullException;
import com.bangbangbwa.backend.domain.member.exception.NotParsedValueException;
import com.bangbangbwa.backend.domain.member.exception.type.MemberErrorType;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class MemberProvider {

  private final MemberReader memberReader;

  public Long getCurrentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (Objects.isNull(authentication)) {
      throw new AuthenticationNullException();
    }
    String name = authentication.getName();
    if (!StringUtils.hasText(name)) {
      throw new AuthenticationNameNullException();
    }
    try {
      return Long.valueOf(name);
    } catch (NumberFormatException e) {
      throw new NotParsedValueException(
          MemberErrorType.NOT_PARSED_VALUE_ERROR.getMessage() + " : " + name);
    }
  }

  public Member getCurrentMember() {
    Long memberId = getCurrentMemberId();
    return memberReader.findById(memberId);
  }

  public Long getCurrentMemberIdIfLogin() {
    try {
      return this.getCurrentMemberId();
    } catch (Exception e) {
      return null;
    }
  }
}