package com.bangbangbwa.backend.domain.members.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberJoinDto {

  @Schema(name = "MemberJoin_Request", description = "회원가입 요청 DTO")
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(description = "인가코드")
    @NotBlank(message = "인가코드를 함께 요청 바랍니다.")
    private String authCode;
    @Schema(description = "닉네임")
    @NotBlank(message = "닉네임을 입력 바랍니다.")
    private String nickname;
  }

  @Schema(name = "MemberJoin_Response", description = "회원가입 응답 DTO")
  @Getter
  @Builder
  public static class Response {

    @Schema(description = "액세스토큰")
    private String accessToken;
    @Schema(description = "리프레쉬토큰")
    private String refreshToken;
  }
}
