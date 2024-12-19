package com.bangbangbwa.backend.domain.post.repository;

import com.bangbangbwa.backend.domain.post.common.entity.PostVisibilityMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostVisibilityMemberRepository {

  private final SqlSession mysql;

  public void saveList(List<PostVisibilityMember> memberList) {
    mysql.insert("PostVisibilityMember.saveList", memberList);
  }


  public List<PostVisibilityMember> findPrivatePostsByMemberId(Long memberId) {
    return mysql.selectList("PostVisibilityMember.findPrivatePostsByMemberId", memberId);
  }

}
