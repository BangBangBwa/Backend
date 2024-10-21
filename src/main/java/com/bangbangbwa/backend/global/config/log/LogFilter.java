package com.bangbangbwa.backend.global.config.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;


@Slf4j
public class LogFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    CachingRequestWrapper requestWrapper = new CachingRequestWrapper(request);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
    final UUID uuid = UUID.randomUUID();

    long startTime = System.currentTimeMillis();
    try {
      MDC.put("uuid", uuid.toString()); // 로그 별 ID 값 추가
      logRequest(requestWrapper);
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      logResponse(responseWrapper, startTime);
      responseWrapper.copyBodyToResponse();
      MDC.clear();
    }
  }

  private void logRequest(HttpServletRequestWrapper request) throws IOException {
    String queryString = request.getQueryString();
    String body = getBody(request.getInputStream());

    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    log.info("Request : {} uri=[{}] content-type=[{}], body=[{}]"
        , request.getMethod()
        ,
        queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString
        , request.getContentType()
        , body);
  }

  private void logResponse(ContentCachingResponseWrapper response, long startTime)
      throws IOException {
    String body = getBody(response.getContentInputStream());

    log.info("Response : {} body=[{}]"
        , response.getStatus()
        , body);
    log.info("Request processed in {}ms", (System.currentTimeMillis() - startTime));
    log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
  }

  public String getBody(InputStream is) throws IOException {
    byte[] content = StreamUtils.copyToByteArray(is);
    if (content.length == 0) {
      return null;
    }
    return new String(content, StandardCharsets.UTF_8);
  }
}