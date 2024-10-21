package com.bangbangbwa.backend.domain.auth.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfoDto {

  private String snsId;
  private String snsType;
  private String oauthToken;

  public OAuthUserInfoDto addOauthToken(String oauthToken) {
    this.oauthToken = oauthToken;
    return this;
  }
}
