package com.bangbangbwa.backend.domain.member.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.exception.NotSignupMemberException;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReader {

  private final MemberRepository memberRepository;

  public Member findBySns(OAuthInfoDto oAuthInfo) {
    String snsId = oAuthInfo.getSnsId();
    SnsType snsType = oAuthInfo.getSnsType();
    return memberRepository.findBySns(snsId, snsType).orElseThrow(
        () -> new NotSignupMemberException(oAuthInfo.getOAuthToken())
    );
  }
}
