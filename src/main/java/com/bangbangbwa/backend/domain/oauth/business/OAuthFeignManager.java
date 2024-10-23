package com.bangbangbwa.backend.domain.oauth.business;

import com.bangbangbwa.backend.domain.oauth.common.GoogleInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.KakaoInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.NaverInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.OAuthInfoDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthFeignManager {

  private final GoogleInfoProvider googleInfoProvider;
  private final KakaoInfoProvider kakaoInfoProvider;
  private final NaverInfoProvider naverInfoProvider;


  public static final String BEARER = "Bearer ";
  public static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
  public static final String GRANT_TYPE = "authorization_code";

  public OAuthInfoDto getGoogleInfo(String oauthToken) throws FeignException {
    GoogleInfoDto googleInfo = googleInfoProvider.getInfo(oauthToken);
    return OAuthInfoDto.builder()
        .snsId(googleInfo.sub())
        .email(googleInfo.email())
        .build();
  }

  public OAuthInfoDto getKakaoInfo(String oauthToken) throws FeignException {
    KakaoInfoDto kakaoInfo = kakaoInfoProvider.getInfo(oauthToken);
    return OAuthInfoDto.builder().build();
  }

  public OAuthInfoDto getNaverInfo(String oauthToken) throws FeignException {
    NaverInfoDto naverInfo = naverInfoProvider.getInfo(oauthToken);
    return OAuthInfoDto.builder().build();
  }
}
