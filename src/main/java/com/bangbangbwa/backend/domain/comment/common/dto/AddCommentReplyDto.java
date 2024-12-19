package com.bangbangbwa.backend.domain.comment.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddCommentReplyDto {

  @Schema(name = "AddCommentReplyRequest", description = "답글 추가 요청 DTO")
  public record Request(
      @Schema(description = "댓글 아이디", example = "1")
      @NotNull(message = "댓글 아이디를 입력 바랍니다.")
      Long commentId,
      @Schema(description = "답글 내용", example = "게시물 작성자의 답변입니다.")
      @NotBlank(message = "답글 내용을 입력 바랍니다.")
      String message
  ) {

  }
}
