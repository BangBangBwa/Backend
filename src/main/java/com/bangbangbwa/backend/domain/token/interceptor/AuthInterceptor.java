package com.bangbangbwa.backend.domain.token.interceptor;

import com.bangbangbwa.backend.domain.token.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

  private final TokenService tokenService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {

    boolean hasAnnotation = hasPreAuthorizeAnnotation(handler);

    if (hasAnnotation) {
      String accessToken = tokenService.getTokenFromAuthorizationHeader(request);

      tokenService.validateAccessToken(accessToken);

      Authentication auth = tokenService.getAuthenticationByAccessToken(accessToken);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    return true;
  }

  private boolean hasPreAuthorizeAnnotation(Object handler) {
    // Swagger 같은 js/html 관련 파일들은 통과한다.(view 관련 요청 = ResourceHttpRequestHandler)
    if (handler instanceof ResourceHttpRequestHandler) {
      return false;
    }

    HandlerMethod handlerMethod = (HandlerMethod) handler;
    return handlerMethod.getMethod().isAnnotationPresent(PreAuthorize.class);
  }
}
