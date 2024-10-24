package com.bangbangbwa.backend.domain.oauth.business;

import com.bangbangbwa.backend.domain.member.exception.InvalidSnsTypeException;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.global.error.exception.ServerException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthInfoProvider {

  private final OAuthFeignManager oAuthFeignManager;

  public OAuthInfoDto getInfo(SnsType snsType, String oauthToken) {
    try {
      switch (snsType) {
        case GOOGLE -> {
          return oAuthFeignManager.getGoogleInfo(oauthToken);
        }
        case KAKAO -> {
          return oAuthFeignManager.getKakaoInfo(oauthToken);
        }
        case NAVER -> {
          return oAuthFeignManager.getNaverInfo(oauthToken);
        }
        default -> throw new InvalidSnsTypeException();
      }
    } catch (FeignException e) {
      log.error("FeignError : ", e);
      throw new ServerException();
    }
  }

  public OAuthInfoDto getInfoByCode(SnsType snsType, String authCode) {
    try {
      switch (snsType) {
        case GOOGLE -> {
          return oAuthFeignManager.getGoogleInfoByCode(authCode);
        }
        case KAKAO -> {
          return oAuthFeignManager.getKakaoInfoByCode(authCode);
        }
        case NAVER -> {
          return oAuthFeignManager.getNaverInfoByCode(authCode);
        }
        default -> throw new InvalidSnsTypeException();
      }
    } catch (FeignException e) {
      log.error("FeignError : ", e);
      throw new ServerException();
    }
  }
}
