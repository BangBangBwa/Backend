package com.bangbangbwa.backend.domain.sns.repository;

import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

  private final SqlSession mysql;

  public void save(Comment comment) {
    mysql.insert("CommentMapper.save", comment); }
}
