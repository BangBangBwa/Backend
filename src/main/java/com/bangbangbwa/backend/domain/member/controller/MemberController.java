package com.bangbangbwa.backend.domain.member.controller;

import com.bangbangbwa.backend.domain.member.common.dto.CommentDto;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponse;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponseCommentInfo;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponsePostInfo;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.Response;
import com.bangbangbwa.backend.domain.member.common.dto.FollowDto;
import com.bangbangbwa.backend.domain.member.common.dto.FollowDto.FollowResponse;
import com.bangbangbwa.backend.domain.member.common.dto.FollowerDto;
import com.bangbangbwa.backend.domain.member.common.dto.FollowerDto.FollowerResponse;
import com.bangbangbwa.backend.domain.member.common.dto.MemberLoginDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberNicknameDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberSignupDto;
import com.bangbangbwa.backend.domain.member.common.dto.MemberUpdateDto;
import com.bangbangbwa.backend.domain.member.common.dto.PostDto;
import com.bangbangbwa.backend.domain.member.common.dto.ProfileDto;
import com.bangbangbwa.backend.domain.member.common.dto.PromoteStreamerDto;
import com.bangbangbwa.backend.domain.member.common.dto.SummaryDto;
import com.bangbangbwa.backend.domain.member.common.dto.ToggleFollowDto;
import com.bangbangbwa.backend.domain.member.common.dto.TogglePostPinDto;
import com.bangbangbwa.backend.domain.member.common.mapper.MemberMapper;
import com.bangbangbwa.backend.domain.member.common.mapper.ProfileMapper;
import com.bangbangbwa.backend.domain.member.common.mapper.SummaryMapper;
import com.bangbangbwa.backend.domain.member.service.MemberService;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.oauth.service.OAuthService;
import com.bangbangbwa.backend.domain.post.common.mapper.PostMapper;
import com.bangbangbwa.backend.domain.streamer.common.entity.PendingStreamer;
import com.bangbangbwa.backend.domain.streamer.common.mapper.PendingStreamerMapper;
import com.bangbangbwa.backend.domain.streamer.service.PendingStreamerService;
import com.bangbangbwa.backend.domain.tag.common.dto.TagDto;
import com.bangbangbwa.backend.domain.tag.service.TagService;
import com.bangbangbwa.backend.domain.token.common.dto.ReissueTokenDto;
import com.bangbangbwa.backend.domain.token.common.dto.TokenDto;
import com.bangbangbwa.backend.domain.token.service.TokenService;
import com.bangbangbwa.backend.global.annotation.authentication.PermitAll;
import com.bangbangbwa.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  private final TagService tagService;

  @PostMapping(value = "/{snsType}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ApiResponse<MemberSignupDto.Response> signup(
      @PathVariable("snsType") SnsType snsType,
      @RequestPart(value = "file", required = false) MultipartFile file,
      @RequestPart @Valid MemberSignupDto.Request request
  ) {
    String oauthToken = request.oauthToken();
    OAuthInfoDto oAuthInfo = oAuthService.getInfoByToken(snsType, oauthToken);
    TokenDto token = memberService.signup(oAuthInfo, request, file);
    List<TagDto> tagList = tagService.getTagList(request.tags(), token.getMember().getId());
    memberService.memberTagRelation(token.getMember(), tagList);
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
  public ApiResponse<MemberNicknameDto.Response> randomNicknames() {
    String nickname = memberService.serveRandomNickname();
    MemberNicknameDto.Response response = new MemberNicknameDto.Response(nickname);
    return ApiResponse.ok(response);
  }

  @PostMapping("/reissueToken")
  public ApiResponse<ReissueTokenDto.Response> reissueToken(@RequestParam String refreshToken) {
    TokenDto tokenDto = tokenService.reissueToken(refreshToken);
    ReissueTokenDto.Response response = MemberMapper.INSTANCE.dtoToReissueResponse(tokenDto);
    return ApiResponse.ok(response);
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
  public ApiResponse<Boolean> isMyMemberId(@PathVariable("memberId") Long memberId) {
    Boolean isMyMemberId = memberService.isMyMemberId(memberId);
    return ApiResponse.ok(isMyMemberId);
  }

  @GetMapping("/profile/{memberId}")
  @PermitAll()
  public ApiResponse<ProfileDto.Response> getProfile(@PathVariable("memberId") Long memberId) {
    ProfileDto profile = memberService.getProfile(memberId);
    ProfileDto.Response response = ProfileMapper.INSTANCE.dtoToResponse(profile);
    return ApiResponse.ok(response);
  }

  @GetMapping("/summary/{memberId}")
  @PermitAll()
  public ApiResponse<SummaryDto.Response> getSummary(@PathVariable("memberId") Long memberId) {
    SummaryDto summaryDto = memberService.getSummary(memberId);
    SummaryDto.Response response = SummaryMapper.INSTANCE.dtoToResponse(summaryDto);
    return ApiResponse.ok(response);
  }

  @GetMapping("/posts/{memberId}")
  @PermitAll()
  public ApiResponse<PostDto.Response> getPosts(@PathVariable("memberId") Long memberId) {
    List<PostDto> postDtos = memberService.getPosts(memberId);
    PostDto.Response response = PostMapper.INSTANCE.dtoToResponse(postDtos);
    return ApiResponse.ok(response);
  }

  @GetMapping("/comments/{memberId}")
  @PermitAll()
  public ApiResponse<Response> getComments(@PathVariable("memberId") Long memberId) {
    List<CommentResponse> commentResponses = new ArrayList<>();
    CommentResponsePostInfo postInfo;
    CommentResponseCommentInfo commentInfo;

    postInfo = new CommentResponsePostInfo(1L, "제목입니다1", memberId, "전정국",
        "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg",
        true, true);
    commentInfo = new CommentResponseCommentInfo(1L, "댓글입니다1", 1L, "답글입니다1");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    postInfo = new CommentResponsePostInfo(2L, "제목입니다2", memberId, "전정국",
        "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg",
        true, false);
    commentInfo = new CommentResponseCommentInfo(2L, "댓글입니다2", 2L, "답글입니다2");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    postInfo = new CommentResponsePostInfo(3L, "제목입니다3", memberId, "전정국",
        "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg",
        false, true);
    commentInfo = new CommentResponseCommentInfo(3L, "댓글입니다3", 3L, "답글입니다3");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    postInfo = new CommentResponsePostInfo(4L, "제목입니다4", memberId, "전정국",
        "https://images.khan.co.kr/article/2024/03/05/news-p.v1.20240305.9dc707937ff0483e9f91ee16c87312dd_P1.jpg",
        false, false);
    commentInfo = new CommentResponseCommentInfo(4L, "댓글입니다4", 4L, "답글입니다4");
    commentResponses.add(new CommentResponse(postInfo, commentInfo));

    CommentDto.Response response = new CommentDto.Response(commentResponses);

    return ApiResponse.ok(response);
  }

  @GetMapping("/followers/{memberId}")
  @PermitAll()
  public ApiResponse<FollowerDto.Response> getFollowers(@PathVariable("memberId") Long memberId) {
    List<FollowerResponse> followerResponses = memberService.getFollowers(memberId);
    FollowerDto.Response response = new FollowerDto.Response(followerResponses);
    return ApiResponse.ok(response);
  }

  @GetMapping("/follows/{memberId}")
  @PermitAll()
  public ApiResponse<FollowDto.Response> getFollows(@PathVariable("memberId") Long memberId) {
    List<FollowResponse> followerResponses = memberService.getFollows(memberId);
    FollowDto.Response response = new FollowDto.Response(followerResponses);
    return ApiResponse.ok(response);
  }

  @PostMapping("/togglePostPin")
  @PreAuthorize("hasAuthority('STREAMER')")
  public ApiResponse<?> togglePostPin(@RequestBody TogglePostPinDto.Request req) {
    memberService.togglePostPin(req);
    return ApiResponse.ok();
  }

  @PostMapping("/toggleFollow")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<?> toggleFollow(@RequestBody ToggleFollowDto.Request req) {
    memberService.toggleFollow(req);
    return ApiResponse.ok();
  }

  @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<ProfileDto.Response> updateMember(
      @RequestPart(value = "file", required = false) MultipartFile file,
      @RequestPart @Valid MemberUpdateDto.Request request
  ) {
    List<TagDto> tagList = tagService.getTagList(request.tagList());
    ProfileDto infoDto = memberService.updateMember(tagList, request, file);
    ProfileDto.Response response = ProfileMapper.INSTANCE.dtoToResponse(infoDto);
    return ApiResponse.ok(response);
  }
}
