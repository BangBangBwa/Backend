package com.bangbangbwa.backend.domain.comment.exception;

import com.bangbangbwa.backend.domain.comment.exception.type.CommentErrorType;
import com.bangbangbwa.backend.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class NotFoundCommentException extends BusinessException {

  private final String code;

  public NotFoundCommentException() {
    this(CommentErrorType.NOT_FOUND_COMMENT.getMessage());
  }

  public NotFoundCommentException(final String message) {
    super(message);
    this.code = CommentErrorType.NOT_FOUND_COMMENT.name();
  }
}
