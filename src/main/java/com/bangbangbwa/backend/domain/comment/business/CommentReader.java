package com.bangbangbwa.backend.domain.comment.business;

import com.bangbangbwa.backend.domain.comment.common.dto.MyPostCommentDto.MyPostCommentResponse;
import com.bangbangbwa.backend.domain.comment.exception.NotFoundCommentException;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponse;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentReader {

  private final CommentRepository commentRepository;

  public Comment findById(Long commentId) {
    return commentRepository.findById(commentId).orElseThrow(NotFoundCommentException::new);
  }

  public List<MyPostCommentResponse> findCommentListByPostId(Long postId) {
    return commentRepository.findCommentListByPostId(postId);
  }

  public List<CommentResponse> findCommentListByMemberId(Long memberId) {
    return commentRepository.findCommentListByMemberId(memberId);
  }
}
