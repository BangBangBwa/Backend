package com.bangbangbwa.backend.domain.sns.controller;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.mapper.MemberMapper;
import com.bangbangbwa.backend.domain.member.service.MemberService;
import com.bangbangbwa.backend.domain.sns.common.dto.CreateCommentDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetLatestPostsDto;
import com.bangbangbwa.backend.domain.sns.common.dto.SearchMemberDto;
import com.bangbangbwa.backend.domain.sns.common.dto.UploadPostMediaDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.common.mapper.CommentMapper;
import com.bangbangbwa.backend.domain.sns.common.mapper.PostMapper;
import com.bangbangbwa.backend.domain.sns.service.SnsService;
import com.bangbangbwa.backend.domain.sns.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetPostListDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.streamer.common.entity.DailyMessage;
import com.bangbangbwa.backend.domain.streamer.service.DailyMessageService;
import com.bangbangbwa.backend.global.annotation.authentication.AuthenticationContext;
import com.bangbangbwa.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/sns")
@RequiredArgsConstructor
public class SnsController implements SnsApi{

  private final SnsService snsService;
  private final DailyMessageService dailyMessageService;

  @GetMapping("/getPostList")
  public ApiResponse<List<GetPostListDto.Response>> getPostList() {
    List<Post> postList = snsService.getPostList(PostType.STREAMER);
    List<GetPostListDto.Response> response = PostMapper.INSTANCE.dtoToGetPostListResponse(postList);
    return ApiResponse.ok(response);
  }

  //note. 팔로우 관련 작업 필요
  @GetMapping("/getPostDetails/{postId}")
  @AuthenticationContext
  public ApiResponse<GetPostDetailsDto.Response> getPostDetails(@PathVariable Long postId) {
    GetPostDetailsDto.Response response = snsService.getPostDetails(postId);
    return ApiResponse.ok(response);
  }

  @PostMapping("/createPost")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<CreatePostDto.Response> createPost(
      @RequestBody @Valid CreatePostDto.Request request
  ) {
    Post post = snsService.createPost(request);
    CreatePostDto.Response response = PostMapper.INSTANCE.dtoToCreatePostResponse(post);
    return ApiResponse.ok(response);
  }

  @PostMapping(value = "/uploadPostMedia", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<UploadPostMediaDto.Response> uploadPostMedia(
      @RequestPart(value = "file", required = false) MultipartFile file
      ) {
    String mediaUrl = snsService.uploadPostMedia(file);
    UploadPostMediaDto.Response response = PostMapper
        .INSTANCE
        .dtoToUploadPostMediaResponse(mediaUrl);
    return ApiResponse.ok(response);
  }

  @PostMapping("/createComment")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<CreateCommentDto.Response> createComment(
      @RequestBody @Valid CreateCommentDto.Request request
  ) {
    Comment comment = snsService.createComment(request);
    CreateCommentDto.Response response = CommentMapper.INSTANCE.dtoToCreateCommentResponse(comment);
    return ApiResponse.ok(response);
  }

  // note. 현재 비회원도 사용 가능한 오류 발생
  @GetMapping("/searchMember/{nickname}")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<List<SearchMemberDto.Response>> searchMember(@PathVariable String nickname) {
    List<Member> memberList = snsService.searchMember(nickname);
    List<SearchMemberDto.Response> response = MemberMapper
        .INSTANCE
        .dtoToSearchNicknameResponse(memberList);
    return ApiResponse.ok(response);
  }

  @GetMapping("/getLatestPosts")
  @AuthenticationContext
  public ApiResponse<List<GetLatestPostsDto.Response>> getLatestPosts() {
    List<GetLatestPostsDto> getLatestPostsDto = snsService.getLatestPosts(PostType.STREAMER);

    List<Long> streamerIds = getLatestPostsDto.stream()
        .map(GetLatestPostsDto::getStreamerId)
        .collect(Collectors.toList());

    List<DailyMessage> dailyMessageList = dailyMessageService.getDailyMessagesByIds(streamerIds);

    List<GetLatestPostsDto.Response> response = IntStream.range(0, getLatestPostsDto.size())
        .mapToObj(i -> getLatestPostsDto.get(i).toResponse(dailyMessageList.get(i)))
        .collect(Collectors.toList());

    return ApiResponse.ok(response);
  }
}
