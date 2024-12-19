package com.bangbangbwa.backend.domain.sns.controller;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.mapper.MemberMapper;
import com.bangbangbwa.backend.domain.post.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetLatestPostsDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.post.common.dto.UploadPostMediaDto;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.post.common.mapper.PostMapper;
import com.bangbangbwa.backend.domain.sns.common.dto.CreateCommentDto;
import com.bangbangbwa.backend.domain.sns.common.dto.ReportCommentDto;
import com.bangbangbwa.backend.domain.sns.common.dto.ReportPostDto;
import com.bangbangbwa.backend.domain.sns.common.dto.SearchMemberDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.common.mapper.CommentMapper;
import com.bangbangbwa.backend.domain.sns.service.SnsService;
import com.bangbangbwa.backend.global.annotation.authentication.PermitAll;
import com.bangbangbwa.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
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
public class SnsController implements SnsApi {

  private final SnsService snsService;

  @GetMapping("/getPostDetails/{postId}")
  @PermitAll()
  public ApiResponse<GetPostDetailsDto.Response> getPostDetails(
      @PathVariable("postId") Long postId) {
    GetPostDetailsDto.Response response = snsService.getPostDetails(postId);
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

  @GetMapping("/searchMember/{nickname}")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<List<SearchMemberDto.Response>> searchMember(
      @PathVariable("nickname") String nickname) {
    List<Member> memberList = snsService.searchMember(nickname);
    List<SearchMemberDto.Response> response = MemberMapper
        .INSTANCE
        .dtoToSearchNicknameResponse(memberList);
    return ApiResponse.ok(response);
  }

  @GetMapping("/getLatestPosts")
  @PermitAll()
  public ApiResponse<List<GetLatestPostsDto.Response>> getLatestPosts() {
    List<GetLatestPostsDto.Response> response = snsService.getLatestPosts(PostType.STREAMER);
    return ApiResponse.ok(response);
  }

  @PostMapping("/reportPost")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<?> reportPost(
      @RequestBody @Valid ReportPostDto.Request request
  ) {
    snsService.reportPost(request);
    return ApiResponse.ok();
  }

  @PostMapping("/reportComment")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<?> reportComment(
      @RequestBody @Valid ReportCommentDto.Request request
  ) {
    snsService.reportComment(request);
    return ApiResponse.ok();
  }
}
