package com.bangbangbwa.backend.domain.auth.business;

import com.bangbangbwa.backend.domain.auth.common.dto.KakaoInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "KakaoResource",
    url = "https://kapi.kakao.com")
public interface KakaoUserClient {
  @GetMapping(value = "/v2/user/me")
  KakaoInfoDto getInfo(
      @RequestHeader(value = "Authorization") String accessToken,
      @RequestHeader(value = "Content-Type", defaultValue = "application/x-www-form-urlencoded;charset=utf-8") String contentType);

  default KakaoInfoDto getInfo(String accessToken) {
    return getInfo("Bearer " + accessToken, "application/x-www-form-urlencoded;charset=utf-8");
  }
}
