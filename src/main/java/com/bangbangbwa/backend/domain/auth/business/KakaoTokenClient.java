package com.bangbangbwa.backend.domain.auth.business;


import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoToken", url = "https://kauth.kakao.com")
public interface KakaoTokenClient {
  @PostMapping(value = "/oauth/token")
  OAuthTokenDto getAccessToken(
      @RequestParam("code") String code,
      @RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret,
      @RequestParam("redirect_uri") String redirectUri,
      @RequestParam("grant_type") String grantType);
}
