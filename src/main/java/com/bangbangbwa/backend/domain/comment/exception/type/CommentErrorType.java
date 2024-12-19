package com.bangbangbwa.backend.domain.comment.exception.type;

import lombok.Getter;

@Getter
public enum CommentErrorType {

  NOT_FOUND_COMMENT("등록되지 않은 답변입니다."),
  ;

  private final String message;

  CommentErrorType(String message) {
    this.message = message;
  }
}
