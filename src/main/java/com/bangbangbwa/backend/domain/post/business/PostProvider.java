package com.bangbangbwa.backend.domain.post.business;

import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.entity.PostVisibilityMember;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.business.MemberPostRecommender;
import com.bangbangbwa.backend.domain.streamer.common.StreamerPostRecommender;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostProvider {

  // 게시물 반환 갯수
  private final int POST_SIZE = 7;


  private final MemberPostRecommender memberPostRecommender;
  private final StreamerPostRecommender streamerPostRecommender;
  private final PostReader postReader;
  private final PostVisibilityMemberReader postVisibilityMemberReader;


  public List<Post> getRandomPost(PostType postType) {
    List<Post> postList = postReader.findPublicPostsByPostTypeWithLimit(postType, POST_SIZE);
    Collections.shuffle(postList);
    return postList;
  }

  public List<Post> getStreamerPersonalizedPosts(Long memberId, Set<String> readPostIds) {
    addPrivatePostToReadPostIds(memberId, readPostIds);
    List<Post> followerPost = getFollowerPost(memberId, readPostIds);
    List<Post> randomPost = getRandomPost(PostType.MEMBER, readPostIds);
    return streamerPostRecommender.getPosts(followerPost, randomPost);
  }

  public List<Post> getMemberPersonalizedPosts(Long memberId, Set<String> readPostIds) {
    addPrivatePostToReadPostIds(memberId, readPostIds);
    List<Post> followPost = getFollowPosts(memberId, readPostIds);
    List<Post> tagPost = getTagPost(memberId, readPostIds);
    List<Post> randomPost = getRandomPost(PostType.STREAMER, readPostIds);
    return memberPostRecommender.getPosts(followPost, tagPost, randomPost);
  }


  private void addPrivatePostToReadPostIds(Long memberId, Set<String> readPostIds) {
    List<PostVisibilityMember> postVisibilityMembers =
        postVisibilityMemberReader.findPrivatePostsByMemberId(memberId);

    postVisibilityMembers.stream()
        .map(post -> post.getPostId().toString())
        .forEach(readPostIds::add);
  }

  private List<Post> getFollowerPost(Long memberId, Set<String> readPostIds) {
    List<Post> posts = postReader.findPostsByFollowerExcludingRead(
        POST_SIZE, memberId, readPostIds
    );
    addReadPostIds(posts, readPostIds);
    return posts;
  }

  private List<Post> getFollowPosts(Long memberId, Set<String> readPostIds) {
    List<Post> posts = postReader.findPostsByFollowStreamerExcludingReadIds(POST_SIZE, memberId,
        readPostIds);
    addReadPostIds(posts, readPostIds);
    return posts;
  }

  private List<Post> getTagPost(Long memberId, Set<String> readPostIds) {
    List<Post> posts = postReader.findPostsByFollowedStreamerExcludingReadIds(POST_SIZE, memberId,
        readPostIds);
    addReadPostIds(posts, readPostIds);
    return posts;
  }

  private List<Post> getRandomPost(PostType postType, Set<String> readPostIds) {
    List<Post> posts = postReader.findRandomPostsExcludingReadIds(postType, POST_SIZE, readPostIds);
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
