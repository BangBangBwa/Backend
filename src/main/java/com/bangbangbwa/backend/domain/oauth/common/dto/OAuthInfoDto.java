package com.bangbangbwa.backend.domain.oauth.common.dto;

import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthInfoDto {

  private String snsId;
  private SnsType snsType;
  private String email;
  private String oAuthToken;
}
