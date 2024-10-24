package com.bangbangbwa.backend.global.error;

import com.bangbangbwa.backend.domain.member.exception.NotSignupMemberException;
import com.bangbangbwa.backend.domain.member.exception.type.MemberErrorType;
import com.bangbangbwa.backend.global.error.exception.BusinessException;
import com.bangbangbwa.backend.global.error.exception.ForbiddenException;
import com.bangbangbwa.backend.global.error.exception.ServerException;
import com.bangbangbwa.backend.global.error.exception.UnAuthenticatedException;
import com.bangbangbwa.backend.global.error.type.ApiErrorType;
import com.bangbangbwa.backend.global.response.ApiResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Bean Validation 예외 처리
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<?> handler(BindException e) {
    List<String> errorMessageList = e.getFieldErrors().stream().map(
        b -> b.getField() + " : " + b.getDefaultMessage()
    ).toList();
    return ApiResponse.error(ApiErrorType.BAD_REQUEST, errorMessageList);
  }

  /**
   * 잘못된 요청인 경우 예외 처리
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<?> handler(BusinessException e) {
    return ApiResponse.error(e.getCode(), e.getMessage());
  }

  /**
   * 인증을 실패 했을 경우 예외 처리
   */
  @ExceptionHandler(UnAuthenticatedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<?> handler(UnAuthenticatedException e) {
    if (e instanceof NotSignupMemberException) {
      return ApiResponse.error(e.getCode(), MemberErrorType.NOT_SIGN_UP_MEMBER.getMessage(),
          e.getMessage());
    }
    return ApiResponse.error(e.getCode(), e.getMessage());
  }

  /**
   * 권한이 없을 경우 예외 처리 AccessDeniedException : Spring Security PreAuthorize 어노테이션의 Exception
   */
  @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiResponse<?> handler(AccessDeniedException e) {
    if (e instanceof ForbiddenException) {
      return ApiResponse.error(((ForbiddenException) e).getCode(), e.getMessage());
    }
    return ApiResponse.error(ApiErrorType.FORBIDDEN.name(), e.getMessage());
  }

  /**
   * 서버에서 에러가 발생 했을 경우 예외 처리
   */
  @ExceptionHandler(ServerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<?> handler(ServerException e) {
    return ApiResponse.error(e.getCode(), e.getMessage());
  }

  /**
   * 이외 예상치 못한 RuntimeException 예외 처리
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<?> handler(Exception e) {
    log.error(e.getMessage());
    log.error("Exception : ", e);
    return ApiResponse.error(ApiErrorType.INTERNAL_SERVER_ERROR);
  }
}
