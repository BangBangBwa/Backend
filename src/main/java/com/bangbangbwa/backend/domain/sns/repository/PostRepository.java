package com.bangbangbwa.backend.domain.sns.repository;

import com.bangbangbwa.backend.domain.sns.common.dto.GetLatestPostsDto;
import com.bangbangbwa.backend.domain.sns.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepository {

  private final SqlSession mysql;

  public void save(Post post) { mysql.insert("PostMapper.save", post); }

  public Optional<Post> findById(Long postId) {
    return Optional.ofNullable(mysql.selectOne("PostMapper.findById", postId));
  }

  public List<Post> findAllByPostType(PostType postType) {
    return mysql.selectList("PostMapper.findAllByPostType", postType);
  }

  public List<Post> findPostsByPostTypeWithLimit(PostType postType, int size) {
    Map<String, Object> params = new HashMap<>();
    params.put("postType", postType);
    params.put("size", size);
    return mysql.selectList("PostMapper.findAllByPostType", params);
  }

  public List<GetLatestPostsDto> findPostsWithinLast24Hours(PostType postType, Set<String> readerPostList) {
    Map<String, Object> params = new HashMap<>();
    params.put("postType", postType);
    params.put("readerPostList", readerPostList);
    return mysql.selectList("PostMapper.findPostsWithinLast24Hours", params);
  }

  public Optional<GetPostDetailsDto.Response> getPostDetails(Long postId, Long memberId) {
    Map<String, Object> params = new HashMap<>();
    params.put("postId", postId);
    params.put("memberId", memberId);
    return Optional.ofNullable(mysql.selectOne("PostMapper.getPostDetails", params));
  }

  public List<Post> findByPostTypeAndRandomPostsExcludingReadIds(PostType postType, int limit, Set<String> readPostIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("postType", postType);
    params.put("limit", limit);
    params.put("readPostIds", readPostIds);
    return mysql.selectList("PostMapper.findByPostTypeAndRandomPostsExcludingReadIds", params);
  }

  public List<Post> findPostsByFollowedStreamerExcludingReadIds(int limit, Long memberId, Set<String> readPostIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", limit);
    params.put("memberId", memberId);
    params.put("readPostIds", readPostIds);
    return mysql.selectList("PostMapper.findPostsByFollowedStreamerExcludingReadIds", params);
  }

  public List<Post> findPostsByFollowStreamerExcludingReadIds(
          int limit,
          Long memberId,
          Set<String> readPostIds
  ) {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", limit);
    params.put("memberId", memberId);
    params.put("readPostIds", readPostIds);
    return mysql.selectList("PostMapper.findPostsByFollowStreamerExcludingReadIds", params);
  }

}

