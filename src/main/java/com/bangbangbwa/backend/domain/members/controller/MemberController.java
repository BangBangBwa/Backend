package com.bangbangbwa.backend.domain.members.controller;

import com.bangbangbwa.backend.domain.auth.common.dto.OAuthTokenDto;
import com.bangbangbwa.backend.domain.auth.common.dto.OAuthUserInfoDto;
import com.bangbangbwa.backend.domain.auth.service.OAuthUserService;
import com.bangbangbwa.backend.domain.members.common.dto.MemberLoginDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 멤버 Controller.
 */
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

  private final OAuthUserService oAuthUserService;

  /**
   * 회원 가입
   */
  @PostMapping("/login/{snsType}")
  public OAuthUserInfoDto signup(@PathVariable("snsType") String snsType,
      @Valid @RequestBody MemberLoginDto.Request request) {
    String authCode = request.getAuthCode();
    OAuthTokenDto oAuthTokenDto = oAuthUserService.getAccessToken(snsType, authCode);
    String accessToken = oAuthTokenDto.accessToken();
    OAuthUserInfoDto oAuthUserInfoDto = oAuthUserService.getOAuthUserInfo(snsType, accessToken);

    return oAuthUserInfoDto;
  }

}
