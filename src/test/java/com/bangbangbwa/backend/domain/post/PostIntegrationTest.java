package com.bangbangbwa.backend.domain.post;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.enums.Role;
import com.bangbangbwa.backend.domain.member.repository.FollowRepository;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostListDto;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.promotion.common.entity.Streamer;
import com.bangbangbwa.backend.domain.promotion.repository.StreamerRepository;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportComment;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import com.bangbangbwa.backend.domain.sns.repository.ReaderPostRepository;
import com.bangbangbwa.backend.domain.sns.repository.ReportCommentRepository;
import com.bangbangbwa.backend.domain.sns.repository.ReportPostRepository;
import com.bangbangbwa.backend.domain.streamer.repository.DailyMessageRepository;
import com.bangbangbwa.backend.domain.streamer.repository.StreamerTagRepository;
import com.bangbangbwa.backend.domain.tag.common.entity.Tag;
import com.bangbangbwa.backend.domain.tag.repository.MemberTagRepository;
import com.bangbangbwa.backend.domain.tag.repository.TagRepository;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.dto.TokenDto;
import com.bangbangbwa.backend.global.response.ApiResponse;
import com.bangbangbwa.backend.global.test.IntegrationTest;
import com.bangbangbwa.backend.global.util.S3Manager;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PostIntegrationTest extends IntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private ReportPostRepository reportPostRepository;

  @Autowired
  private ReportCommentRepository reportCommentRepository;

  @Autowired
  private DailyMessageRepository dailyMessageRepository;

  @Autowired
  private ReaderPostRepository readerPostRepository;

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private MemberTagRepository memberTagRepository;

  @Autowired
  private StreamerRepository streamerRepository;

  @Autowired
  private StreamerTagRepository streamerTagRepository;

  @MockBean
  private S3Manager s3Manager;

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

  private Follow getFollow(Long followerId, Long followeeId) {
    return Follow.builder()
        .followerId(followerId)
        .followeeId(followeeId)
        .build();
  }

  private Follow createFollow(Member followerId, Member followeeId) {
    Follow follow = getFollow(followerId.getId(), followeeId.getId());
    followRepository.save(follow);
    return follow;
  }

  private ReportPost getReportPost(Long postId, String memberId) {
    return ReportPost.builder()
        .createdId(memberId)
        .postId(postId)
        .reason(RandomValue.string(3, 5).setNullable(false).get())
        .build();
  }

  private ReportPost createReportPost(Post post, Member member) {
    ReportPost reportPost = getReportPost(post.getId(), member.getId().toString());
    reportPostRepository.save(reportPost);
    return reportPost;
  }

  private Comment getComment(Post post, Member member) {
    return Comment.builder()
        .content(RandomValue.string(30).setNullable(false).get())
        .memberId(member.getId())
        .postId(post.getId())
        .build();
  }

  private Comment createComment(Post post, Member member) {
    Comment comment = getComment(post, member);
    commentRepository.save(comment);
    return comment;
  }

  private ReportComment getReportComment(Comment comment, Member member) {
    return ReportComment.builder()
        .commentId(comment.getId())
        .createdId(member.getId().toString())
        .build();
  }

  private ReportComment createReportComment(Comment comment, Member member) {
    ReportComment reportComment = getReportComment(comment, member);
    reportCommentRepository.save(reportComment);
    return reportComment;
  }

  private Tag getTag() {
    String name = RandomValue.string(10, 15).setNullable(false).get();
    return Tag.builder().createdId("test").name(name).build();
  }

  private Tag createTag() {
    Tag tag = getTag();
    tagRepository.save(tag);
    return tag;
  }

  private Streamer getStreamer(Member member) {
    return Streamer.builder().memberId(member.getId()).build();
  }

  private Streamer createStreamer(Member member) {
    Streamer streamer = getStreamer(member);
    streamerRepository.save(streamer);
    return streamer;
  }


  @Test()
  void getPostList_토큰_보유_멤버() {
    // given
    int POST_SIZE = 7;
    Role memberRole = Role.MEMBER;
    PostType postType = PostType.STREAMER;
    Member member = getMember();
    member.updateRole(memberRole);
    memberRepository.save(member);

    Tag tag = createTag();
    memberTagRepository.save(member.getId(), tag.getId());

    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    int postCount = RandomValue.getInt(0, 5);
    List<Post> postList = IntStream.range(0, postCount)
        .mapToObj(i -> createPost(postType, writeMember))
        .toList();

    Member writeFollowMember = createMember();
    createFollow(member, writeFollowMember);
    int followPostCount = RandomValue.getInt(0, 5);
    List<Post> followPostList = IntStream.range(0, followPostCount)
        .mapToObj(i -> createPost(postType, writeFollowMember))
        .toList();

    Member writeTagMember = createMember();
    Streamer streamer = createStreamer(writeTagMember);
    streamerRepository.save(streamer);
    streamerTagRepository.save(streamer.getId(), tag);
    int tagPostCount = RandomValue.getInt(0, 5);
    List<Post> tagPostList = IntStream.range(0, tagPostCount)
        .mapToObj(i -> createPost(postType, writeTagMember))
        .toList();

    int totalPostCount = Math.min(POST_SIZE, tagPostCount + followPostCount + postCount);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    String url = "http://localhost:" + port + "/api/v1/posts/getPostList";

    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        String.class
    );

    ApiResponse<List<GetPostListDto.Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<GetPostListDto.Response>>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().size()).isEqualTo(totalPostCount);

  }


  @Test
  void getPostList_토큰_미입력() {
    // given
    PostType postType = PostType.STREAMER;

    Member writeMember = createMember();
    int postCount = RandomValue.getInt(0, 5);

    IntStream.range(0, postCount)
        .forEach(i -> createPost(postType, writeMember));

    String url = "http://localhost:" + port + "/api/v1/posts/getPostList";

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
        url,
        String.class
    );

    ApiResponse<List<GetPostListDto.Response>> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<List<GetPostListDto.Response>>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().size()).isEqualTo(postCount);

  }
}