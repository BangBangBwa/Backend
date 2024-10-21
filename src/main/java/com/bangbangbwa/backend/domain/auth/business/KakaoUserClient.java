package com.bangbangbwa.backend.domain.auth.business;

import com.bangbangbwa.backend.domain.auth.common.dto.KakaoInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "KakaoResource", url = "https://kapi.kakao.com")
public interface KakaoUserClient {

  String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded;charset=utf-8";
  String BEARER_PREFIX = "Bearer ";

  @GetMapping(value = "/v2/user/me")
  KakaoInfoDto getInfo(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken,
      @RequestHeader(value = HttpHeaders.CONTENT_TYPE, defaultValue = CONTENT_TYPE_VALUE) String contentType);

  default KakaoInfoDto getInfo(String accessToken) {
    return getInfo(BEARER_PREFIX + accessToken, CONTENT_TYPE_VALUE);
  }
}
