package com.bangbangbwa.backend.domain.members.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberLoginDto {

  @Schema(name = "MemberLogin_Request", description = "로그인 요청 DTO")
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(description = "인가코드")
    @NotBlank(message = "인가코드를 입력 바랍니다.")
    private String authCode;
  }

  @Schema(name = "MemberLogin_Response", description = "로그인 응답 DTO")
  @Getter
  @Builder
  public static class Response {

    @Schema(description = "액세스토큰")
    private String accessToken;
    @Schema(description = "리프레쉬토큰")
    private String refreshToken;
  }
}
