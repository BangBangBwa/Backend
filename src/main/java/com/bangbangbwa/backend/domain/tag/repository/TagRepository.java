package com.bangbangbwa.backend.domain.tag.repository;

import com.bangbangbwa.backend.domain.tag.common.dto.TagDto;
import com.bangbangbwa.backend.domain.tag.common.entity.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TagRepository {

  private final SqlSession mysql;

  @Transactional
  public void save(Tag tag) {
    mysql.insert("TagMapper.save", tag);
  }

  public Optional<Tag> findByName(String name) {
    return Optional.ofNullable(mysql.selectOne("TagMapper.findByName", name));
  }

  public List<String> findAllByName(String tagWord) {
    return mysql.selectList("TagMapper.findAllByName", tagWord);
  }

  public List<TagDto> findByMemberId(Long memberId) {
    return mysql.selectList("TagMapper.findByMemberId", memberId);
  }

  public List<TagDto> findByStreamerId(Long streamerId) {
    return mysql.selectList("TagMapper.findByStreamerId", streamerId);
  }
}
