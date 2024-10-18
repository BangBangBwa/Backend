package com.bangbangbwa.backend.domain.auth.business;

import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthUserInfoDto;
import com.bangbangbwa.backend.domain.auth.common.exception.NotSupportedSnsTypeException;
import com.bangbangbwa.backend.domain.members.common.exception.OAuth2FeignException;
import com.bangbangbwa.backend.domain.members.common.exception.UriSyntaxException;
import feign.FeignException;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthUserInfoProvider {

  private final KakaoUserProvider kakaoUserProvider;


  public OAuthUserInfoDto provideOAuthUserInfoByAuthCode(String snsType, String authCode) {
    try {
      OAuthUserInfoDto oAuthUserInfoDto = requestUserInfo(snsType, authCode);
      return oAuthUserInfoDto.addOauthToken(authCode);
    } catch (URISyntaxException e) {
      throw new UriSyntaxException();
    } catch (FeignException e) {
      logErrorMessage(e);
      throw new OAuth2FeignException();
    }
  }

  public OAuthTokenDto getAccessToken(String snsType, String authCode) {
    try {
      return requestToken(snsType, authCode);
    } catch (URISyntaxException e) {
      throw new UriSyntaxException();
    }
  }

  private void logErrorMessage(FeignException e) {
    log.error("\n url : {}, \n method : {}, \n content : {}", e.request().url(),
        e.request().httpMethod().name(), e.contentUTF8());
  }

  private OAuthTokenDto requestToken(String snsType, String authCode)
      throws URISyntaxException, FeignException {
    return switch (snsType) {
      case "google" -> null;
      case "kakao" -> kakaoUserProvider.getAccessToken(authCode);
      case "naver" -> null;
      default -> throw new NotSupportedSnsTypeException();
    };

  }

  private OAuthUserInfoDto requestUserInfo(String snsType, String accessToken)
      throws URISyntaxException, FeignException {
    return switch (snsType) {
      case "google" -> null;
      case "kakao" -> kakaoUserProvider.getOAuthInfo(accessToken);
      case "naver" -> null;
      default -> throw new NotSupportedSnsTypeException();
    };
  }
}
