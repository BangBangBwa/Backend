package com.bangbangbwa.backend.domain.oauth.business;

import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.GRANT_TYPE;

import com.bangbangbwa.backend.domain.oauth.business.feign.NaverFeign;
import com.bangbangbwa.backend.domain.oauth.common.dto.NaverInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NaverInfoProvider {

  private final NaverFeign naverFeign;
  private final String NAVER_CLIENT_ID;
  private final String NAVER_CLIENT_SECRET;
  private final String NAVER_REDIRECT_URI;
  private final String NAVER_GRANT_TYPE;

  NaverInfoProvider(
      NaverFeign naverFeign,
      @Value("${oauth.client.naver.client-id}") String naverClientId,
      @Value("${oauth.client.naver.client-secret}") String naverClientSecret,
      @Value("${oauth.client.naver.redirect-url}") String naverRedirectUri
  ) {
    this.naverFeign = naverFeign;
    this.NAVER_CLIENT_ID = naverClientId;
    this.NAVER_CLIENT_SECRET = naverClientSecret;
    this.NAVER_REDIRECT_URI = naverRedirectUri;
    this.NAVER_GRANT_TYPE = GRANT_TYPE;
  }

  public NaverInfoDto getInfo(String oauthToken) {
    return null;
  }
}