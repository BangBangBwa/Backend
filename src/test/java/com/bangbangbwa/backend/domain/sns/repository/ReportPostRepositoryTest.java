package com.bangbangbwa.backend.domain.sns.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bangbangbwa.backend.domain.admin.common.dto.GetReportedPostsDto.GetReportedPostsResponse;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import com.bangbangbwa.backend.global.test.MyBatisTest;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({ReportPostRepository.class, MemberRepository.class, PostRepository.class})
class ReportPostRepositoryTest extends MyBatisTest {

  @Autowired
  private ReportPostRepository reportPostRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private PostRepository postRepository;


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

  private ReportPost getReportPost(Post post, Member writeMember) {
    return ReportPost.builder()
        .postId(post.getId())
        .reason(RandomValue.string(3,10).setNullable(false).get())
        .createdId(String.valueOf(writeMember.getId()))
        .build();
  }
  private ReportPost createReportPost(Post post, Member writeMember) {
    ReportPost reportPost = getReportPost(post, writeMember);
    reportPostRepository.save(reportPost);
    return reportPost;
  }


  @Test
  void save() {
    // given
    Member member = createMember();
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);
    createReportPost(post, member);

    // then && when
    ReportPost reportPost = getReportPost(post, writeMember);
    reportPostRepository.save(reportPost);
  }

  @Test
  void findByPostIdAndCreatedId() {
  }

  @Test
  void findAllPendingReports() {
    // given
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    int reportPostCount = RandomValue.getInt(3,5);

    List<Member> members = IntStream.range(0, reportPostCount)
        .mapToObj(i -> createMember())
        .toList();
    List<Post> posts = members.stream()
        .map(writeMember -> createPost(postType, writeMember))
        .toList();
    List<ReportPost> reportPosts = IntStream.range(0, reportPostCount)
        .mapToObj(i -> createReportPost(posts.get(i), members.get(i)))
        .toList();

    // then
    List<GetReportedPostsResponse> getReportPosts = reportPostRepository.findAllPendingReports();

    // when
    assertThat(getReportPosts.size()).isEqualTo(reportPostCount);
    IntStream.range(0, reportPostCount)
        .forEach(i -> {
          assertThat(getReportPosts.get(i).postId()).isEqualTo(posts.get(i).getId());
          assertThat(getReportPosts.get(i).writerId()).isEqualTo(members.get(i).getId());
          assertThat(getReportPosts.get(i).nickname()).isEqualTo(members.get(i).getNickname());
          assertThat(getReportPosts.get(i).profile()).isEqualTo(members.get(i).getProfile());
          assertThat(getReportPosts.get(i).reason()).isEqualTo(reportPosts.get(i).getReason());
          assertThat(getReportPosts.get(i).reportDate().withNano(0)).isEqualTo(reportPosts.get(i).getCreatedAt().withNano(0));
        });
  }

  @Test()
  void findById() {
    // given
    Member member = createMember();
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);
    ReportPost reportPost = createReportPost(post, member);

    // when
    ReportPost getReportPost = reportPostRepository.findById(reportPost.getId())
        .orElseThrow(AssertionError::new);

    // then
    assertThat(getReportPost.getId()).isEqualTo(reportPost.getId());
    assertThat(getReportPost).usingRecursiveComparison().isEqualTo(reportPost);
  }
}