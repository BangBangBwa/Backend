package com.bangbangbwa.backend.domain.post.business;

import com.bangbangbwa.backend.domain.member.common.dto.PostDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetLatestPostsDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.exception.NotFoundPostException;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostReader {

  private final PostRepository postRepository;

  public Post findById(Long postId) {
    return postRepository.findById(postId).orElseThrow(NotFoundPostException::new);
  }

  public GetPostDetailsDto.Response getPostDetails(Long postId, Long memberId) {
    return postRepository.getPostDetails(postId, memberId).orElseThrow(NotFoundPostException::new);
  }

  public List<Post> findAllByPostType(PostType postType) {
    return postRepository.findAllByPostType(postType);
  }

  public List<Post> findPublicPostsByPostTypeWithLimit(PostType postType, int size) {
    return postRepository.findPublicPostsByPostTypeWithLimit(postType, size);
  }

  public List<GetLatestPostsDto> findPostsWithinLast24Hours(PostType postType,
      Set<String> readPostIds) {
    return postRepository.findPostsWithinLast24Hours(postType, readPostIds);
  }

  public List<Post> findRandomPostsExcludingReadIds(PostType postType, int limit,
      Set<String> excludedPostIds) {
    return postRepository.findByPostTypeAndRandomPostsExcludingReadIds(postType, limit,
        excludedPostIds);
  }

  public List<Post> findPostsByFollowedStreamerExcludingReadIds(int limit, Long memberId,
      Set<String> excludedPostIds) {
    return postRepository.findPostsByFollowedStreamerExcludingReadIds(limit, memberId,
        excludedPostIds);
  }

  public List<Post> findPostsByFollowStreamerExcludingReadIds(
      int limit,
      Long memberId,
      Set<String> excludedPostIds
  ) {
    return postRepository.findPostsByFollowStreamerExcludingReadIds(limit, memberId,
        excludedPostIds);
  }

  public List<Post> findPostsByFollowerExcludingRead(
      int limit, Long memberId, Set<String> excludedPostIds
  ) {
    return postRepository.findPostsByFollowerExcludingRead(limit, memberId, excludedPostIds);
  }

  public List<PostDto> findPostsByMemberId(Long memberId) {
    return postRepository.findPostsByMemberId(memberId);
  }

}
