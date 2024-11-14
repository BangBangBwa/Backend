package com.bangbangbwa.backend.domain.sns.business;

import com.bangbangbwa.backend.domain.sns.common.entity.PostVisibilityMember;
import com.bangbangbwa.backend.domain.sns.repository.PostVisibilityMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostVisibilityMemberCreator {

  private final PostVisibilityMemberRepository postVisibilityMemberRepository;

  public void saveVisibilityMemberList(List<PostVisibilityMember> postVisibilityMemberList) {
    postVisibilityMemberRepository.publicMemberListSave(postVisibilityMemberList);
  }
}
