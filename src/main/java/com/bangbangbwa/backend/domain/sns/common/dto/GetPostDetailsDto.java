package com.bangbangbwa.backend.domain.sns.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

public class GetPostDetailsDto {

  public record Response(
      @Schema(description = "게시물ID")
      Long postId,
      @Schema(description = "작성자ID")
      Long writerId,
      @Schema(description = "제목")
      String title,
      @Schema(description = "내용")
      String content,
      @Schema(description = "프로필 사진")
      String profileUrl,
      @Schema(description = "닉네임")
      String nickname,
      @Schema(description = "내가 작성한 댓글")
      String comment
//      @Schema(description = "팔로우 여부")
//      boolean isFollowed
  ) {}
}
