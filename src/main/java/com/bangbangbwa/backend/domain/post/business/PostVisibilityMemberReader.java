package com.bangbangbwa.backend.domain.post.business;

import com.bangbangbwa.backend.domain.post.common.entity.PostVisibilityMember;
import com.bangbangbwa.backend.domain.post.repository.PostVisibilityMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostVisibilityMemberReader {

  private final PostVisibilityMemberRepository postVisibilityMemberRepository;

  public List<PostVisibilityMember> findPrivatePostsByMemberId(Long memberId) {
    return postVisibilityMemberRepository.findPrivatePostsByMemberId(memberId);
  }

}
