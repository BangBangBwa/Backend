package com.bangbangbwa.backend.domain.member.controller;

import com.bangbangbwa.backend.domain.member.common.dto.MemberLoginDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.global.annotation.swagger.ApiCommonResponse;
import com.bangbangbwa.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "MemberAPI", description = "회원 API")
@ApiCommonResponse
public interface MemberApi {

  @Operation(summary = "회원가입", tags = {"MemberAPI"})
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200", description = "OK",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MemberSignupDto.Response.class)
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "잘못된 요청입니다.",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ApiResponse.class)),
              examples = {
                  @ExampleObject(
                      value = """
                          {
                          "code": "BAD_REQUEST",
                          "message": "잘못된 요청",
                          "data" : [
                            "oAuthToken : oAuth 토큰을 입력해주세요.",
                            "nickname : 닉네임을 입력해주세요.",
                            "nickname : 최대 12자 이하로 입력해주세요.",
                            "nickname : 한글,영문,숫자, 특수문자('(',')','-','_')만 사용 가능합니다."
                          ]
                          }
                          """)
              }
          )
      )
  })
  ApiResponse<MemberSignupDto.Response> signup(
      @Parameter SnsType snsType,
      @RequestBody MultipartFile file,
      @RequestBody MemberSignupDto.Request request
  );

  @Operation(summary = "로그인", tags = {"MemberAPI"})
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200", description = "OK",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MemberLoginDto.Response.class)
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401", description = "미가입 회원",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                      "code": "UNAUTHORIZED",
                      "message": "가입되지 않은 회원입니다.",
                      "data" : "ya29.a0AeDClZAJN53QRLtXX-jiAjQBVLdpBvp3NwkWNi9rITYZKVsj2dvy-7z0CqyvGZC_gR6a-tmjEZLNyXY3-u-srtSzmSzeRBAaPIFXpJdK47F0KH2nMrQhFEF6cXKiXcAT6_49H3QMRbBE1TPht5VYNolFLfU67QyFrmdNh0dwaCgYKAcISARMSFQHGX2MiQHTvShJ8pEyM9Ui51m2Gww0175"
                      }"""
              )
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "잘못된 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                      "code": "BAD_REQUEST",
                      "message": "잘못된 요청입니다.",
                      "data" : [
                        "authCode: 인가코드를 입력해주세요."
                      ]
                      }
                      """
              )
          )
      )
  })
  ApiResponse<MemberLoginDto.Response> login(
      @Parameter SnsType snsType,
      @RequestBody MemberLoginDto.Request request
  );
}
