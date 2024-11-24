package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreator {

  private final CommentRepository commentRepository;

  public void save(Comment comment) {
    commentRepository.save(comment); }
}
