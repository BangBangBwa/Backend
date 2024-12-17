package com.bangbangbwa.backend.domain.admin.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public class GetReportedPostsDto {

  @Schema(name = "GetReportedPostsResponse", description = "신고된 게시물 조회 응답")
  public record Response(
      List<GetReportedPostsResponse> posts
  ){ }

  @Schema(name = "GetReportedPostsResponses")
  public record GetReportedPostsResponse(
      @Schema(description = "신고 ID")
      Long reportPostId,
      @Schema(description = "게시물 ID")
      Long postId,
      @Schema(description = "작성자 ID")
      Long writerId,
      @Schema(description = "작성자 닉네임")
      String nickname,
      @Schema(description = "작성자 프로필 URL")
      String profile,
      @Schema(description = "신고 사유")
      String reason,
      @Schema(description = "신고 날짜")
      LocalDateTime reportDate
  ) { }

}
