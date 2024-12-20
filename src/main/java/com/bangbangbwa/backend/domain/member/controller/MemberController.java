package com.bangbangbwa.backend.domain.member.controller;

import com.bangbangbwa.backend.domain.member.common.dto.CommentDto;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponse;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponseCommentInfo;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponsePostInfo;
import com.bangbangbwa.backend.domain.member.common.dto.FollowerDto;
import com.bangbangbwa.backend.domain.member.common.dto.FollowerDto.FollowerResponse;
import com.bangbangbwa.backend.domain.member.common.dto.MemberLoginDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberNicknameDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.dto.PostDto;
import com.bangbangbwa.backend.domain.member.common.dto.PostDto.PostResponse;
import com.bangbangbwa.backend.domain.member.common.dto.ProfileDto;
import com.bangbangbwa.backend.domain.member.common.dto.PromoteStreamerDto;
import com.bangbangbwa.backend.domain.member.common.dto.SummaryDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

  @GetMapping("/isMyMemberId/{memberId}")
  public ApiResponse<Boolean> isMyMemberId(@PathVariable("memberId") String memberId) {
    Boolean isMyMemberId = memberService.isMyMemberId(memberId);
    return ApiResponse.ok(isMyMemberId);
  }

  @GetMapping("/profile/{memberId}")
  public ApiResponse<ProfileDto.Response> getProfile(@PathVariable("memberId") String memberId) {
    ProfileDto.Response response;

    response = new ProfileDto.Response(
        "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg",
        "전정국",
        new Random().nextBoolean(),
        "안녕하세요. 전정국입니다.",
        null
    );
    return ApiResponse.ok(response);
  }

  @GetMapping("/summary/{memberId}")
  public ApiResponse<SummaryDto.Response> getSummary(@PathVariable("memberId") String memberId) {
    SummaryDto.Response response = new SummaryDto.Response(
        123L,
        456L,
        789L,
        new Random().nextBoolean(),
        new Random().nextBoolean(),
        null
    );
    return ApiResponse.ok(response);
  }

  @GetMapping("/posts/{memberId}")
  public ApiResponse<PostDto.Response> getPosts(@PathVariable("memberId") String memberId) {
    List<PostResponse> postResponses = new ArrayList<>();
    postResponses.add(new PostResponse(1L, true, "제목입니다1.", "내용입니다1.", "2024.01.01", true, true));
    postResponses.add(new PostResponse(2L, true, "제목입니다2.", "내용입니다2.", "2024.04.24", true, false));
    postResponses.add(new PostResponse(3L, new Random().nextBoolean(), "제목입니다3.", "내용입니다3.", "2024.07.05", false, true));
    postResponses.add(new PostResponse(4L, false, "제목입니다4.", "내용입니다4.", "2024.09.15", false, false));
    PostDto.Response response = new PostDto.Response(postResponses);
    return ApiResponse.ok(response);
  }

  @GetMapping("/comments/{memberId}")
  public ApiResponse<CommentDto.Response> getComments(@PathVariable("memberId") String memberId) {
    long id = 1L;
    try {
      id = Long.parseLong(memberId);
    } catch (NumberFormatException ignored) {}
    List<CommentResponse> commentResponses = new ArrayList<>();
    CommentResponsePostInfo postInfo;
    CommentResponseCommentInfo commentInfo;

    postInfo = new CommentResponsePostInfo(1L, "제목입니다1", id, "전정국", "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg", true, true);
    commentInfo = new CommentResponseCommentInfo(1L, "댓글입니다1", 1L, "답글입니다1");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    postInfo = new CommentResponsePostInfo(2L, "제목입니다2", id, "전정국", "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg", true, false);
    commentInfo = new CommentResponseCommentInfo(2L, "댓글입니다2", 2L, "답글입니다2");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    postInfo = new CommentResponsePostInfo(3L, "제목입니다3", id, "전정국", "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg", false, true);
    commentInfo = new CommentResponseCommentInfo(3L, "댓글입니다3", 3L, "답글입니다3");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    postInfo = new CommentResponsePostInfo(4L, "제목입니다4", id, "전정국", "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg", false, false);
    commentInfo = new CommentResponseCommentInfo(4L, "댓글입니다4", 4L, "답글입니다4");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    CommentDto.Response response = new CommentDto.Response(commentResponses);

    return ApiResponse.ok(response);
  }

  @GetMapping("/followers/{memberId}")
  public ApiResponse<FollowerDto.Response> getFollowers(@PathVariable("memberId") String memberId) {
    List<FollowerResponse> followerResponses = new ArrayList<>();

    followerResponses.add(new FollowerResponse(1L, "전정국", "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg"));
    followerResponses.add(new FollowerResponse(2L, "정국", "https://theviewers.co.kr/Files/30/News/202308/2890_20230816082125807.JPEG"));
    followerResponses.add(new FollowerResponse(3L, "JK", "https://img.etoday.co.kr/pto_db/2023/04/600/20230425161027_1877523_1200_1655.jpg"));

    FollowerDto.Response response = new FollowerDto.Response(followerResponses);

    return ApiResponse.ok(response);
  }
}
