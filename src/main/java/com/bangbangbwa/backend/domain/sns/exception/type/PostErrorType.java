package com.bangbangbwa.backend.domain.sns.exception.type;

import lombok.Getter;

@Getter
public enum PostErrorType {
  NO_POST_PERMISSION("작성 권한이 없습니다."),
  NOT_FOUND_POST("존재하지 않는 게시물 입니다."),
  NOT_FOUND_REPORT("존재하지 않는 신고 내역 입니다"),
  INVALID_MEMBER_VISIBILITY("공개/비공개 값은 함께 전달할 수 없습니다."),
  NOT_FOUND_POST_OR_PERMISSION("존재하지 않거나 작성 권한이 없는 게시물입니다."),
  MAX_PINNED_POSTS_EXCEEDED("고정할 수 있는 최대 게시물을 초과하였습니다."),

  DUPLICATE_PENDING_PROMOTION("승인 대기 중인 신청이 이미 존재합니다."),
  DUPLICATE_REPORT("이전에 신고한 내용이 처리 중입니다. 잠시 후 다시 시도해 주세요.");

  private final String message;

  PostErrorType(String message) {
    this.message = message;
  }
}
