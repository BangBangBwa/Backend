package com.bangbangbwa.backend.domain.member.repository;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

  private final SqlSession mysql;

  public void save(Member member) {
    mysql.insert("MemberMapper.save", member);
  }

  public Optional<Member> findBySns(String snsId, SnsType snsType) {
    Map<String, Object> params = new HashMap<>();
    params.put("snsId", snsId);
    params.put("snsType", snsType);
    return Optional.ofNullable(mysql.selectOne("MemberMapper.findBySns", params));
  }
}