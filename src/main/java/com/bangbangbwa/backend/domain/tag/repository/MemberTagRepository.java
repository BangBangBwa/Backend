package com.bangbangbwa.backend.domain.tag.repository;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MemberTagRepository {

    private final SqlSession mysql;

    @Transactional
    public void save(Long memberId, Long tagId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("tagId", tagId);
        mysql.insert("MemberTagMapper.save", params);
    }
}
