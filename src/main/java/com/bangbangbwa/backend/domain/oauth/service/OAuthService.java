package com.bangbangbwa.backend.domain.oauth.service;

import com.bangbangbwa.backend.domain.oauth.business.OAuthInfoProvider;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

  private final OAuthInfoProvider oAuthInfoProvider;

  public OAuthInfoDto getInfoByToken(SnsType snsType, String oauthToken) {
    return oAuthInfoProvider.getInfo(snsType, oauthToken);
  }

  public OAuthInfoDto getInfoByCode(SnsType snsType, String authCode) {
    return oAuthInfoProvider.getInfoByCode(snsType, authCode);
  }
}
