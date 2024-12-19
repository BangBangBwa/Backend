package com.bangbangbwa.backend.domain.comment.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentUpdater {

  private final CommentRepository commentRepository;

  public void updateMemberId(Comment comment, Member member) {
    comment.updateMemberId(member.getId());
  }

  public void updateForAddReply(Comment comment) {
    commentRepository.update(comment);
  }
}
