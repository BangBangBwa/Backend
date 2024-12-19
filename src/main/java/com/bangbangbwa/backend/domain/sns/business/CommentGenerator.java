package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.comment.business.CommentUpdater;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.sns.common.dto.CreateCommentDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentGenerator {

  private final CommentParser commentParser;
  private final CommentUpdater commentUpdater;

  public Comment generate(CreateCommentDto.Request request, Member member) {
    Comment comment = commentParser.requestToEntity(request);
    commentUpdater.updateMemberId(comment, member);
    return comment;
  }
}
