package com.bangbangbwa.backend.domain.oauth.business;

import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.BEARER;
import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.CONTENT_TYPE;
import static com.bangbangbwa.backend.domain.oauth.business.OAuthFeignManager.GRANT_TYPE;

import com.bangbangbwa.backend.domain.oauth.business.feign.KakaoInfoFeign;
import com.bangbangbwa.backend.domain.oauth.business.feign.KakaoTokenFeign;
import com.bangbangbwa.backend.domain.oauth.common.dto.KakaoInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.dto.KakaoTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoInfoProvider {

  private final KakaoInfoFeign kakaoInfoFeign;
  private final KakaoTokenFeign kakaoTokenFeign;
  private final String KAKAO_CLIENT_ID;
  private final String KAKAO_CLIENT_SECRET;
  private final String KAKAO_REDIRECT_URI;
  private final String KAKAO_GRANT_TYPE;

  KakaoInfoProvider(
      KakaoInfoFeign kakaoInfoFeign,
      KakaoTokenFeign kakaoTokenFeign,
      @Value("${oauth.client.kakao.client-id}") String kakaoClientId,
      @Value("${oauth.client.kakao.client-secret}") String kakaoClientSecret,
      @Value("${oauth.client.kakao.redirect-url}") String kakaoRedirectUri) {
    this.kakaoInfoFeign = kakaoInfoFeign;
    this.kakaoTokenFeign = kakaoTokenFeign;
    this.KAKAO_CLIENT_ID = kakaoClientId;
    this.KAKAO_CLIENT_SECRET = kakaoClientSecret;
    this.KAKAO_REDIRECT_URI = kakaoRedirectUri;
    this.KAKAO_GRANT_TYPE = GRANT_TYPE;
  }

  public KakaoInfoDto getInfo(String accessToken) {
    return kakaoInfoFeign.requestInfo(BEARER + accessToken, CONTENT_TYPE);
  }

  public KakaoTokenDto getInfoByCode(String authCode) {
    return kakaoTokenFeign.getAccessToken(
        authCode,
        KAKAO_CLIENT_ID,
        KAKAO_CLIENT_SECRET,
        KAKAO_REDIRECT_URI,
        KAKAO_GRANT_TYPE
    );
  }
}
