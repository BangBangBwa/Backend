package com.bangbangbwa.backend.domain.oauth.business.feign;

import com.bangbangbwa.backend.domain.oauth.common.dto.GoogleTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "GoogleToken", url = "https://oauth2.googleapis.com/token")
public interface GoogleTokenFeign {

  @PostMapping
  GoogleTokenDto requestToken(
      @RequestParam("code") String code,
      @RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret,
      @RequestParam("redirect_uri") String redirectUri,
      @RequestParam("grant_type") String grantType);

}
