package com.bangbangbwa.backend.domain.sns.exception;

import com.bangbangbwa.backend.domain.sns.exception.type.PostErrorType;
import com.bangbangbwa.backend.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class NotFoundReportException extends BusinessException {

  private final String code;

  public NotFoundReportException() {
    this(PostErrorType.NOT_FOUND_REPORT.getMessage());
  }

  public NotFoundReportException(String message) {
    super(message);
    this.code = PostErrorType.NOT_FOUND_REPORT.name();
  }
}
