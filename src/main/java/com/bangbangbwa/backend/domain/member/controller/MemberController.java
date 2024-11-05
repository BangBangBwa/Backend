package com.bangbangbwa.backend.domain.member.controller;

import com.bangbangbwa.backend.domain.member.common.dto.MemberLoginDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberNicknameDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.dto.PromoteStreamerDto;
import com.bangbangbwa.backend.domain.member.common.mapper.MemberMapper;
import com.bangbangbwa.backend.domain.member.service.MemberService;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.oauth.service.OAuthService;
import com.bangbangbwa.backend.domain.streamer.common.entity.PendingStreamer;
import com.bangbangbwa.backend.domain.streamer.common.mapper.PendingStreamerMapper;
import com.bangbangbwa.backend.domain.streamer.service.PendingStreamerService;
import com.bangbangbwa.backend.domain.token.common.TokenDto;
import com.bangbangbwa.backend.domain.token.service.TokenService;
import com.bangbangbwa.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController implements MemberApi {

  private final OAuthService oAuthService;
  private final MemberService memberService;
  private final TokenService tokenService;
  private final PendingStreamerService pendingStreamerService;

  @PostMapping(value = "/{snsType}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ApiResponse<MemberSignupDto.Response> signup(
//      @ValidEnum(enumClass = SnsType.class, message = "지원하지 않는 SNS 타입입니다.") todo : 추가 로직 수정 필요. -> 현재 오류 발생함.
      @PathVariable("snsType") SnsType snsType,
      @RequestPart(value = "file", required = false) MultipartFile file,
      @RequestPart @Valid MemberSignupDto.Request request
  ) {
    String oauthToken = request.oauthToken();
    OAuthInfoDto oAuthInfo = oAuthService.getInfoByToken(snsType, oauthToken);
//    note : 테스트를 위한 코드 추후 삭제 예정
//    OAuthInfoDto oAuthInfo = OAuthInfoDto.builder()
//        .snsType(SnsType.GOOGLE).snsId("").email("").build();
    TokenDto token = memberService.signup(oAuthInfo, request, file);
    MemberSignupDto.Response response = MemberMapper.INSTANCE.dtoToSignupResponse(token);
    return ApiResponse.ok(response);
  }

  @PostMapping("/login/{snsType}")
  public ApiResponse<MemberLoginDto.Response> login(
      @PathVariable SnsType snsType,
      @RequestBody @Valid MemberLoginDto.Request request
  ) {
    String authCode = request.authCode();
    OAuthInfoDto oAuthInfo = oAuthService.getInfoByCode(snsType, authCode);
    TokenDto token = memberService.login(oAuthInfo);
    MemberLoginDto.Response response = MemberMapper.INSTANCE.dtoToLoginResponse(token);
    return ApiResponse.ok(response);
  }

  @GetMapping("/check/{nickname}")
  public ApiResponse<Null> dupCheck(@PathVariable("nickname") String nickname) {
    memberService.checkNickname(nickname);
    return ApiResponse.ok();
  }

  @GetMapping("/nicknames")
  public ApiResponse<MemberNicknameDto.Response> randomNicknames(
      @RequestParam("count") int count
  ) {
    Set<String> nicknames = memberService.serveRandomNicknames(count);
    MemberNicknameDto.Response response = new MemberNicknameDto.Response(nicknames);
    return ApiResponse.ok(response);
  }

  @PostMapping("/reissueToken")
  public ApiResponse<TokenDto> reissueToken(@RequestParam String refreshToken) {
    TokenDto tokenDto = tokenService.reissueToken(refreshToken);
    return ApiResponse.ok(tokenDto);
  }

  @PostMapping("/promoteStreamer")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<PromoteStreamerDto.Response> promoteStreamer(
      @RequestBody @Valid PromoteStreamerDto.Request request
  ) {
    PendingStreamer pendingStreamer = pendingStreamerService.promoteStreamer(request);
    PromoteStreamerDto.Response response = PendingStreamerMapper
        .INSTANCE
        .dtoToPromoteStreamerResponse(pendingStreamer);

    return ApiResponse.ok(response);
  }
}
