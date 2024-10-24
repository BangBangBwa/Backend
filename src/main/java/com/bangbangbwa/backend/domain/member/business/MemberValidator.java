package com.bangbangbwa.backend.domain.member.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.exception.NotSignupMemberException;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {

  private final MemberReader memberReader;

  public Member validate(OAuthInfoDto oAuthInfo) {
    return memberReader.findBySns(oAuthInfo).orElseThrow(
        () -> new NotSignupMemberException(oAuthInfo.getOAuthToken())
    );
  }
}