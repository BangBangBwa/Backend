package com.bangbangbwa.backend.domain.post.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.entity.PostVisibilityMember;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.common.enums.VisibilityType;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import com.bangbangbwa.backend.global.test.MyBatisTest;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({PostVisibilityMemberRepository.class, MemberRepository.class, PostRepository.class})
class PostVisibilityMemberRepositoryTest extends MyBatisTest {

  @Autowired
  private PostVisibilityMemberRepository postVisibilityMemberRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private MemberRepository memberRepository;


  private Member getMember() {
    Member member = Member.builder()
        .nickname(RandomValue.string(255).setNullable(false).get())
        .build();
    OAuthInfoDto oAuthInfo = OAuthInfoDto.builder()
        .snsId(RandomValue.string(255).setNullable(false).get())
        .email(RandomValue.string(255).setNullable(false).getEmail())
        .snsType(RandomValue.getRandomEnum(SnsType.class))
        .build();
    member.addOAuthInfo(oAuthInfo);
    member.updateProfile(RandomValue.string(255).get());

    return member;
  }

  private Member createMember() {
    Member member = getMember();
    memberRepository.save(member);
    return member;
  }

  private Post getPost(PostType postType, Member member) {
    return Post.builder()
        .postType(postType)
        .memberId(member.getId())
        .title(RandomValue.string(100).setNullable(false).get())
        .content(RandomValue.string(2000).setNullable(false).get())
        .build();
  }

  private Post createPost(PostType postType, Member writeMember) {
    Post post = getPost(postType, writeMember);
    postRepository.save(post);
    return post;
  }

  private List<PostVisibilityMember> getPostVisibilityMember(Post post, Member member, VisibilityType type) {
    return Collections.singletonList(PostVisibilityMember.builder()
        .postId(post.getId())
        .memberId(member.getId())
        .type(type)
        .createdId("id")
        .build());
  }

  private List<PostVisibilityMember> createPostVisibilityMembers(Post post, Member member, VisibilityType type) {
    List<PostVisibilityMember> postVisibilityMembers = getPostVisibilityMember(post, member, type);
    postVisibilityMemberRepository.saveList(postVisibilityMembers);
    return postVisibilityMembers;
  }

  @Test
  void findPrivatePostsByMemberId() {
    // given
    Member member = createMember();

    Member writerMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);

    int publicCount = RandomValue.getInt(0,5);
    int privateCount = RandomValue.getInt(0,5);

    IntStream.range(0, publicCount).forEach(i -> {
      Post p = createPost(postType, writerMember);
      createPostVisibilityMembers(p, member, VisibilityType.PUBLIC);
    });
    IntStream.range(0, privateCount).forEach(i -> {
      Post p = createPost(postType, writerMember);
      createPostVisibilityMembers(p, member, VisibilityType.PRIVATE);
    });

    // when
    List<PostVisibilityMember> postVisibilityMembers =
        postVisibilityMemberRepository.findPrivatePostsByMemberId(member.getId());

    // then
    assertThat(postVisibilityMembers.size()).isEqualTo(privateCount);
  }
}