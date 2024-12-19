package com.bangbangbwa.backend.domain.sns.common.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("comment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

  private final String SELF = "SELF";

  private Long id;
  private Long postId;
  private Long memberId;
  private String content;
  private String replyComment;
  private String createdId;
  private LocalDateTime createdAt;
  private String updatedId;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  @Builder
  public Comment(Long postId, Long memberId, String content) {
    this.postId = postId;
    this.memberId = memberId;
    this.content = content;
    this.createdId = SELF;
    this.createdAt = LocalDateTime.now();
  }

  public void updateMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public void updateReplyComment(String replyComment) {
    this.replyComment = replyComment;
    this.updatedId = SELF;
    this.updatedAt = LocalDateTime.now();
  }
}
