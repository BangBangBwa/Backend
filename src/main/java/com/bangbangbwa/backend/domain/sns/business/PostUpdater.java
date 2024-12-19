package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.enums.MediaType;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostUpdater {

  private final PostRepository postRepository;

  public void updateMemberId(Post post, Member member) {
    post.updateMemberId(member.getId());
  }

  public void updateMediaType(Post post, MediaType mediaType) {
    post.updateMediaType(mediaType);
  }

  public void updatePostPin(Long postId, boolean pinned) {
    postRepository.updatePostPin(postId, pinned);
  }

  public void updateForDeletion(Post post) {
    postRepository.updatePost(post);
  }
}
