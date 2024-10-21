package com.bangbangbwa.backend.domain.members.controller;//package com.bangbangbwa.backend.domain.test;


import com.bangbangbwa.backend.domain.auth.business.KakaoUserClient;
import com.bangbangbwa.backend.domain.auth.business.KakaoUserProvider;
import com.bangbangbwa.backend.domain.auth.common.dto.KakaoInfoDto;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestLogin {

  private final KakaoUserProvider kakaoUserProvider;
  private final KakaoUserClient kakaoUserClient;

  @GetMapping("/kakao")
  public ResponseEntity<KakaoInfoDto> kakaoLoginCallback(@RequestParam String code) {

    OAuthTokenDto tokenResponse = kakaoUserProvider.getAccessToken(code);
    String accessToken = tokenResponse.accessToken();

    System.out.println("AccessToken : " +accessToken);
    KakaoInfoDto userInfo = kakaoUserClient.getInfo(accessToken);

    System.out.println("kakaoId : " + userInfo.getId());
    return ResponseEntity.ok(null);
  }
}

