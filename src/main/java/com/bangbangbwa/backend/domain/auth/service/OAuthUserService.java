package com.bangbangbwa.backend.domain.auth.service;


import com.bangbangbwa.backend.domain.auth.business.OAuthUserInfoProvider;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthUserInfoDto;
import com.bangbangbwa.backend.domain.members.common.dto.MemberLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthUserService {

  private final OAuthUserInfoProvider oauthUserInfoProvider;


  public OAuthTokenDto getAccessToken(String snsType, String authCode) {
    return oauthUserInfoProvider.getAccessToken(snsType,authCode);
  }
  public OAuthUserInfoDto getOAuthUserInfo(String snsType, String accessToken) {
    return oauthUserInfoProvider.provideOAuthUserInfoByAuthCode(snsType, accessToken);
  }
}
