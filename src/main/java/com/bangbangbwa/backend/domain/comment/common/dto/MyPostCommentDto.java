package com.bangbangbwa.backend.domain.comment.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class MyPostCommentDto {

  @Schema(name = "CommentResponse", description = "댓글 목록 조회 응답")
  public record Response(
      @Schema(description = "댓글 정보 목록")
      List<MyPostCommentResponse> comments
  ) {

  }

  @Schema(name = "CommentResponses")
  public record MyPostCommentResponse(
      @Schema(description = "댓글 ID")
      Long commentId,
      @Schema(description = "댓글 내용")
      String content,
      @Schema(description = "작성자 ID")
      Long memberId,
      @Schema(description = "작성자 닉네임")
      String memberNickname,
      @Schema(description = "작성자 이미지 URL")
      String memberProfile,
      @Schema(description = "답글 내용")
      String replyContent
  ) {

  }
}
