package com.bangbangbwa.backend.global.response;

import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

  @Nonnull
  private final String code;

  @Nonnull
  private final String message;

  private final T data;

  private ApiResponse(@Nonnull String code, @Nonnull String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> ApiResponse<T> ok() {
    return ok(null);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<T>(HttpStatus.OK.name(), HttpStatus.OK.getReasonPhrase(), data);
  }

  public static <T> ApiResponse<T> error(ApiErrorType errorType) {
    return error(errorType.name(), errorType.getMessage());
  }

  public static <T> ApiResponse<T> error(ApiErrorType errorType, T data) {
    return error(errorType.name(), errorType.getMessage(), data);
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return error(code, message, null);
  }

  public static <T> ApiResponse<T> error(String code, String message, T data) {
    return new ApiResponse<T>(code, message, data);
  }
}
