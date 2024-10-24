package com.bangbangbwa.backend.domain.member.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MemberSignupDto {

  @Schema(name = "SignupRequest", description = "회원가입 요청 DTO")
  public record Request(

      @Schema(description = "OAuth 토큰", examples = "AV)$M@LC>D>GM12)c(faEF")

      @NotBlank(message = "oauth 토큰을 입력해주세요.")
      String oauthToken,

      @Schema(description = "닉네임", examples = "nick")

      @NotBlank(message = "닉네임을 입력해주세요.")
      @Size(max = 12, message = "최대 {max}자 이하로 입력해주세요.")
      @Pattern(regexp = "^[A-Za-z가-힣0-9()\\-_]+$", message = "한글,영문,숫자, 특수문자('(',')','-','_')만 사용 가능합니다.")
      String nickname

//      @Schema(description = "관심 분야", examples = {"국내여행", "해외여행", "마인크래프트"})
//      List<String> interests
  ) {

  }

  @Schema(name = "SignupResponse", description = "회원가입 응답 DTO")
  public record Response(
      @Schema(description = "액세스 토큰", examples = "xxx.xxx.xxx")
      String accessToken,
      @Schema(description = "리프레쉬 토큰", examples = "xxx.xxx.xxx")
      String refreshToken
  ) {

  }
}
