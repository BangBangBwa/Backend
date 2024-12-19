package com.bangbangbwa.backend.domain.sns.repository;

import com.bangbangbwa.backend.domain.comment.common.dto.MyPostCommentDto.MyPostCommentResponse;
import com.bangbangbwa.backend.domain.member.common.dto.CommentDto.CommentResponse;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

  private final SqlSession mysql;

  public void save(Comment comment) {
    mysql.insert("CommentMapper.save", comment);
  }

  public Optional<Comment> findById(Long commentId) {
    return Optional.ofNullable(mysql.selectOne("CommentMapper.findById", commentId));
  }

  public void update(Comment comment) {
    mysql.update("CommentMapper.updateForAddReply", comment);
  }

  public List<MyPostCommentResponse> findCommentListByPostId(Long postId) {
    return mysql.selectList("CommentMapper.findCommentListByPostId", postId);
  }

  public List<CommentResponse> findCommentListByMemberId(Long memberId) {
    return mysql.selectList("CommentMapper.findCommentListByMemberId", memberId);
  }
}
