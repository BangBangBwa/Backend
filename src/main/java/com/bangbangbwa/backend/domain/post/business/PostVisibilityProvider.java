package com.bangbangbwa.backend.domain.post.business;

import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.entity.PostVisibilityMember;
import com.bangbangbwa.backend.domain.sns.business.PostVisibilityMemberCreator;
import com.bangbangbwa.backend.domain.sns.business.PostVisibilityMemberGenerator;
import com.bangbangbwa.backend.domain.sns.common.enums.VisibilityType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostVisibilityProvider {

  private final PostValidator postValidator;
  private final PostVisibilityMemberGenerator postVisibilityMemberGenerator;
  private final PostVisibilityMemberCreator postVisibilityMemberCreator;

  public void saveVisibilityIfPresent(Post post, List<Long> publicMembers,
      List<Long> privateMembers) {
    VisibilityType type = postValidator.validateMembers(publicMembers, privateMembers);

    if (type != null) {
      List<Long> memberList = getMemberListByVisibilityType(type, publicMembers, privateMembers);
      List<PostVisibilityMember> postVisibilityMember = generatePostVisibilityMembers(
          post, type, memberList
      );
      savePostVisibilityMembers(postVisibilityMember);
    }
  }

  private List<Long> getMemberListByVisibilityType(VisibilityType type, List<Long> publicMembers,
      List<Long> privateMembers) {
    return (type == VisibilityType.PRIVATE) ? privateMembers : publicMembers;
  }

  private List<PostVisibilityMember> generatePostVisibilityMembers(Post post, VisibilityType type,
      List<Long> memberList) {
    return postVisibilityMemberGenerator.generate(post, type, memberList);
  }

  private void savePostVisibilityMembers(List<PostVisibilityMember> postVisibilityMember) {
    postVisibilityMemberCreator.saveList(postVisibilityMember);
  }
}
