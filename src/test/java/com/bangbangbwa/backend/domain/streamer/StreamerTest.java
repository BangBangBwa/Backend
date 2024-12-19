package com.bangbangbwa.backend.domain.streamer;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.enums.Role;
import com.bangbangbwa.backend.domain.member.repository.FollowRepository;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostListDto.Response;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.enums.MediaType;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.dto.TokenDto;
import com.bangbangbwa.backend.global.response.ApiResponse;
import com.bangbangbwa.backend.global.test.IntegrationTest;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class StreamerTest extends IntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private TokenProvider tokenProvider;
  @Autowired
  private FollowRepository followRepository;

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
    Post post = Post.builder()
        .postType(postType)
        .memberId(member.getId())
        .title(RandomValue.string(100).setNullable(false).get())
        .content(RandomValue.string(2000).setNullable(false).get())
        .build();
    post.updateMediaType(RandomValue.getRandomEnum(MediaType.class));
    return post;
  }

  private Post createPost(PostType postType, Member writeMember) {
    Post post = getPost(postType, writeMember);
    postRepository.save(post);
    return post;
  }

  private Follow getFollow(Long followeeId, Long followerId) {
    return Follow.builder()
        .followeeId(followeeId)
        .followerId(followerId)
        .build();
  }

  private Follow createFollow(Long followeeId, Long followerId) {
    Follow follow = getFollow(followeeId, followerId);
    followRepository.save(follow);
    return follow;
  }

  @Test()
  void getPostList_토큰_보유_멤버() {
    // given
    int POST_SIZE = 7;
    Role memberRole = Role.STREAMER;
    Member member = getMember();
    member.updateRole(memberRole);
    memberRepository.save(member);
    TokenDto tokenDto = tokenProvider.getToken(member);

    PostType postType = PostType.MEMBER;

    Member writeMember = createMember();
    int postCount = RandomValue.getInt(0, 5);
    List<Post> postList = IntStream.range(0, postCount)
        .mapToObj(i -> createPost(postType, writeMember))
        .toList();

    int followerCount = RandomValue.getInt(0, 5);
    List<Post> followerPostList = IntStream.range(0, followerCount)
        .mapToObj(i -> {
          Member followerMember = createMember();
          createFollow(member.getId(), followerMember.getId());
          return createPost(postType, followerMember);
        }).toList();

    int totalPostCount = Math.min(POST_SIZE, followerCount + postCount);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    String url = "http://localhost:" + port + "/api/v1/streamer/getPostList";

    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        String.class
    );

    ApiResponse<List<Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<Response>>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().size()).isEqualTo(totalPostCount);

  }

}