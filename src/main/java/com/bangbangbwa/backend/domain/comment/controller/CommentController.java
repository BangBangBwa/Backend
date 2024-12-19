package com.bangbangbwa.backend.domain.comment.controller;

import com.bangbangbwa.backend.domain.comment.common.dto.AddCommentReplyDto;
import com.bangbangbwa.backend.domain.comment.service.CommentService;
import com.bangbangbwa.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/reply")
  @PreAuthorize("hasAuthority('MEMBER')")
  public ApiResponse<Null> addCommentReply(
      @RequestBody @Valid AddCommentReplyDto.Request request
  ) {
    commentService.addCommentReply(request);
    return ApiResponse.ok();
  }
}
