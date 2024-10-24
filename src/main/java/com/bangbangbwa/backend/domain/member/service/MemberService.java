package com.bangbangbwa.backend.domain.member.service;

import com.bangbangbwa.backend.domain.member.business.MemberCreator;
import com.bangbangbwa.backend.domain.member.business.MemberGenerator;
import com.bangbangbwa.backend.domain.member.business.MemberValidator;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberGenerator memberGenerator;
  private final MemberValidator memberValidator;
  private final MemberCreator memberCreator;
  private final TokenProvider tokenProvider;

  @Transactional
  public TokenDto signup(OAuthInfoDto oAuthInfo, MemberSignupDto.Request request,
      MultipartFile profileFile) {
    Member member = memberGenerator.generate(oAuthInfo, request, profileFile);
    memberCreator.save(member);
    return tokenProvider.getToken(member);
  }

  public TokenDto login(OAuthInfoDto oAuthInfo) {
    Member member = memberValidator.validate(oAuthInfo);
    return tokenProvider.getToken(member);
  }
}
