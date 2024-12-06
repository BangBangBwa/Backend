package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostProvider {

  // 게시물 반환 최대 갯수
  private final int POST_SIZE = 7;
  // 팔로우 게시물 반환 갯수
  private int FOLLOW_POST_SIZE;
  // 태그 게시물 반환 갯수
  private int TAG_POST_SIZE;
  // 현재까지 조회된 게시물
  private Set<String> excludedPostIds = new HashSet<>();


  private final PostRecommendationStrategy postRecommendationStrategy;
  private final PostReader postReader;


  public List<Post> getRandomPost(PostType postType) {
    List<Post> postList = postReader.findPostsByPostTypeWithLimit(postType, POST_SIZE);
    Collections.shuffle(postList);
    return postList;
  }

  public List<Post> getMemberPersonalizedPosts(Long memberId, Set<String> readPostIds) {
    initializePostSetup(readPostIds);
    List<Post> followPost = getFollowPosts(memberId);
    List<Post> tagPost = getTagPost(memberId);
    List<Post> randomPost = getRandomPostsForMissingCount(PostType.STREAMER);
    return mergeUniquePosts(followPost, tagPost, randomPost);
  }

  private void initializePostSetup(Set<String> readPostIds) {
    setPostSize();
    setExcludedPostIds(readPostIds);
  }

  private void setPostSize() {
    int[] postProbability = postRecommendationStrategy.getPostReturnCount(POST_SIZE);
    FOLLOW_POST_SIZE = postProbability[0];
    TAG_POST_SIZE = postProbability[1];
  }

  private void setExcludedPostIds(Set<String> readPostIds) {
    excludedPostIds.addAll(readPostIds);
  }


  private List<Post> getFollowPosts(Long memberId) {
    List<Post> postList = postReader.findPostsByFollowStreamerExcludingReadIds(FOLLOW_POST_SIZE, memberId, excludedPostIds);
    addExcludedPostIds(postList);
    return postList;
  }

  public List<Post> getTagPost(Long memberId) {
    List<Post> postList = postReader.findPostsByFollowedStreamerExcludingReadIds(TAG_POST_SIZE, memberId, excludedPostIds);
    addExcludedPostIds(postList);
    return postList;
  }

  private List<Post> getRandomPostsForMissingCount(PostType postType) {
    int postSize = POST_SIZE - excludedPostIds.size();
    return postReader.findRandomPostsExcludingReadIds(postType,postSize, excludedPostIds);
  }

  public void addExcludedPostIds(List<Post> postList) {
    excludedPostIds.addAll(postList.stream()
            .map(post -> Long.toString(post.getId()))
            .collect(Collectors.toSet()));
  }

  private List<Post> mergeUniquePosts(List<Post> followPost, List<Post> tagPost, List<Post> randomPost) {
    Set<Post> uniquePosts = new HashSet<>();
    uniquePosts.addAll(followPost);
    uniquePosts.addAll(tagPost);
    uniquePosts.addAll(randomPost);
    return new ArrayList<>(uniquePosts);
  }

}
