package com.bangbangbwa.backend.domain.auth.business;

import com.bangbangbwa.backend.domain.auth.common.dto.KakaoInfoDto;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthUserInfoDto;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoUserProvider {

  private final KakaoTokenClient kakaoTokenClient;
  private final KakaoUserClient kakaoUserClient;
  private final String NAVER_CLIENT_ID;
  private final String NAVER_CLIENT_SECRET;
  private final String NAVER_REDIRECT_URI;
  private final String NAVER_GRANT_TYPE;
  KakaoUserProvider(
      KakaoTokenClient kakaoTokenClient,
      KakaoUserClient kakaoUserClient,
      @Value("${oauth2.client.registration.naver.client-id}") String clientId,
      @Value("${oauth2.client.registration.naver.client-secret}") String clientSecret,
      @Value("http://localhost:8080/test/kakao") String redirectUri,
      @Value("authorization_code") String grantType
  ) {
    this.kakaoTokenClient = kakaoTokenClient;
    this.kakaoUserClient = kakaoUserClient;
    this.NAVER_CLIENT_ID = clientId;
    this.NAVER_CLIENT_SECRET = clientSecret;
    this.NAVER_REDIRECT_URI = redirectUri;
    this.NAVER_GRANT_TYPE = grantType;
  }




  public OAuthTokenDto getAccessToken(String authCode)
      throws FeignException {

    return kakaoTokenClient.getAccessToken(
        authCode,
        NAVER_CLIENT_ID,
        NAVER_CLIENT_SECRET,
        NAVER_REDIRECT_URI,
        NAVER_GRANT_TYPE
    );
  }

  public OAuthUserInfoDto getOAuthInfo(String accessToken) {
    KakaoInfoDto info = kakaoUserClient.getInfo(accessToken);

    return OAuthUserInfoDto.builder()
        .snsId(info.getId())
        .snsType("kakao")
        .build();
  }

}
