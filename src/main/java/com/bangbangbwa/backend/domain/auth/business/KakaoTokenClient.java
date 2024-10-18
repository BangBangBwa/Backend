package com.bangbangbwa.backend.domain.auth.business;


import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "KakaoOAuth", url = "https://kauth.kakao.com")
public interface KakaoTokenClient {
  @PostMapping(
      value = "/oauth/token?" +
          "code={CODE}" +
          "&client_id={CLIENT_ID}" +
          "&client_secret={CLIENT_SECRET}" +
          "&redirect_uri={REDIRECT_URI}" +
          "&grant_type={GRANT_TYPE}")
  OAuthTokenDto getAccessToken(
      @PathVariable("CODE") String code,
      @PathVariable("CLIENT_ID") String clientId,
      @PathVariable("CLIENT_SECRET") String clientSecret,
      @PathVariable("REDIRECT_URI") String redirectUri,
      @PathVariable("GRANT_TYPE") String grantType);
}
