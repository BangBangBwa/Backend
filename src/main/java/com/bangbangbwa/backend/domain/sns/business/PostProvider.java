package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostProvider {

  // 게시물 반환 갯수
  private final int POST_SIZE = 7;


  private final PostRecommendationStrategy postRecommendationStrategy;
  private final PostReader postReader;


  public List<Post> getRandomPost(PostType postType) {
    List<Post> postList = postReader.findPostsByPostTypeWithLimit(postType, POST_SIZE);
    Collections.shuffle(postList);
    return postList;
  }

  public List<Post> getMemberPersonalizedPosts(Long memberId, Set<String> readPostIds) {
    List<Post> followPost = getFollowPosts(memberId, readPostIds);
    List<Post> tagPost = getTagPost(memberId, readPostIds);
    List<Post> randomPost = getRandomPost(PostType.STREAMER, readPostIds);
    return postRecommendationStrategy.getPosts(followPost, tagPost, randomPost);
  }


  private List<Post> getFollowPosts(Long memberId, Set<String> readPostIds) {
    List<Post> posts = postReader.findPostsByFollowStreamerExcludingReadIds(POST_SIZE, memberId, readPostIds);
    addReadPostIds(posts, readPostIds);
    return posts;
  }

  private List<Post> getTagPost(Long memberId, Set<String> readPostIds) {
    List<Post> posts =  postReader.findPostsByFollowedStreamerExcludingReadIds(POST_SIZE, memberId, readPostIds);
    addReadPostIds(posts, readPostIds);
    return posts;
  }

  private List<Post> getRandomPost(PostType postType, Set<String> readPostIds) {
    List<Post> posts =  postReader.findRandomPostsExcludingReadIds(postType,POST_SIZE, readPostIds);
    addReadPostIds(posts, readPostIds);
    return posts;
  }

  private void addReadPostIds(List<Post> posts, Set<String> readPostIds) {
    posts.stream()
        .map(Post::getId)
        .map(String::valueOf)
        .forEach(readPostIds::add);
  }
}
