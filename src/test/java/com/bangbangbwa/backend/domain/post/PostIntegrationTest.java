package com.bangbangbwa.backend.domain.post;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.common.enums.Role;
import com.bangbangbwa.backend.domain.member.repository.FollowRepository;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.post.common.dto.CreatePostDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostDetailsDto;
import com.bangbangbwa.backend.domain.post.common.dto.GetPostListDto;
import com.bangbangbwa.backend.domain.post.common.entity.Post;
import com.bangbangbwa.backend.domain.post.common.entity.PostVisibilityMember;
import com.bangbangbwa.backend.domain.post.common.enums.MediaType;
import com.bangbangbwa.backend.domain.post.common.enums.PostType;
import com.bangbangbwa.backend.domain.post.repository.PostVisibilityMemberRepository;
import com.bangbangbwa.backend.domain.promotion.common.entity.Streamer;
import com.bangbangbwa.backend.domain.promotion.repository.StreamerRepository;
import com.bangbangbwa.backend.domain.sns.common.entity.Comment;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportComment;
import com.bangbangbwa.backend.domain.sns.common.entity.ReportPost;
import com.bangbangbwa.backend.domain.sns.common.enums.VisibilityType;
import com.bangbangbwa.backend.domain.sns.exception.NotFoundPostException;
import com.bangbangbwa.backend.domain.sns.exception.InvalidMemberVisibilityException;
import com.bangbangbwa.backend.domain.sns.repository.CommentRepository;
import com.bangbangbwa.backend.domain.sns.repository.PostRepository;
import com.bangbangbwa.backend.domain.sns.repository.ReaderPostRepository;
import com.bangbangbwa.backend.domain.sns.repository.ReportCommentRepository;
import com.bangbangbwa.backend.domain.sns.repository.ReportPostRepository;
import com.bangbangbwa.backend.domain.streamer.repository.DailyMessageRepository;
import com.bangbangbwa.backend.domain.tag.common.entity.Tag;
import com.bangbangbwa.backend.domain.tag.repository.MemberTagRepository;
import com.bangbangbwa.backend.domain.tag.repository.StreamerTagRepository;
import com.bangbangbwa.backend.domain.tag.repository.TagRepository;
import com.bangbangbwa.backend.domain.token.business.TokenProvider;
import com.bangbangbwa.backend.domain.token.common.dto.TokenDto;
import com.bangbangbwa.backend.domain.token.common.exception.AuthenticationRequiredException;
import com.bangbangbwa.backend.global.response.ApiResponse;
import com.bangbangbwa.backend.global.test.IntegrationTest;
import com.bangbangbwa.backend.global.util.S3Manager;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.ArrayList;
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

  @Autowired
  private PostVisibilityMemberRepository postVisibilityMemberRepository;

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
    streamerTagRepository.save(streamer.getId(), tag.getId());
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


  @Test
  void getPostList_토큰_미입력_비공개_게시물() {
    // given
    PostType postType = PostType.STREAMER;
    Member writeMember = createMember();

    int postCount = RandomValue.getInt(0, 5);
    int publicCount = RandomValue.getInt(0, 5);
    int privateCount = RandomValue.getInt(0, 5);

    IntStream.range(0, postCount)
        .forEach(i -> createPost(postType, writeMember));

    IntStream.range(0, publicCount).forEach(i -> {
      Post p = createPost(postType, writeMember);
      createPostVisibilityMembers(p, writeMember, VisibilityType.PUBLIC);
    });
    IntStream.range(0, privateCount).forEach(i -> {
      Post p = createPost(postType, writeMember);
      createPostVisibilityMembers(p, writeMember, VisibilityType.PRIVATE);
    });

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

  @Test()
  void getPostList_토큰_보유_비공개_게시물() {
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

    int publicCount = RandomValue.getInt(0, 5);
    IntStream.range(0, publicCount).forEach(i -> {
      Post p = createPost(postType, writeMember);
      createPostVisibilityMembers(p, writeMember, VisibilityType.PUBLIC);
    });

    int privateCount = RandomValue.getInt(0, 5);
    IntStream.range(0, privateCount).forEach(i -> {
      Post p = createPost(postType, writeMember);
      createPostVisibilityMembers(p, writeMember, VisibilityType.PRIVATE);
    });

    Member writeFollowMember = createMember();
    createFollow(member, writeFollowMember);
    int followPostCount = RandomValue.getInt(0, 5);
    List<Post> followPostList = IntStream.range(0, followPostCount)
        .mapToObj(i -> createPost(postType, writeFollowMember))
        .toList();

    Member writeTagMember = createMember();
    Streamer streamer = createStreamer(writeTagMember);
    streamerRepository.save(streamer);
    streamerTagRepository.save(streamer.getId(), tag.getId());
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


  // note. PostType 랜덤 지정하도록 변경 필요
  @Test
  void createPost_성공() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    List<Long> nullList = new ArrayList<>();
    CreatePostDto.Request request = new CreatePostDto.Request(
        PostType.MEMBER,
        RandomValue.string(100).setNullable(false).get(),
        RandomValue.string(2000).setNullable(false).get(),
        nullList,
        nullList
    );

    String url = "http://localhost:" + port + "/api/v1/posts/createPost";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<CreatePostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        requestEntity,
        String.class
    );

    ApiResponse<CreatePostDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreatePostDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postType()).isEqualTo(request.postType());
    assertThat(apiResponse.getData().title()).isEqualTo(request.title());
    assertThat(apiResponse.getData().content()).isEqualTo(request.content());

  }


  @Test
  void createPost_공개_비공개_중복_입력() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    List<Long> memberIds = List.of(1L, 2L);
    CreatePostDto.Request request = new CreatePostDto.Request(
        PostType.MEMBER,
        RandomValue.string(100).setNullable(false).get(),
        RandomValue.string(2000).setNullable(false).get(),
        memberIds,
        memberIds
    );

    String url = "http://localhost:" + port + "/api/v1/posts/createPost";

    InvalidMemberVisibilityException exception = new InvalidMemberVisibilityException();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<CreatePostDto.Request> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        requestEntity,
        String.class
    );

    ApiResponse<CreatePostDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreatePostDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertNull(apiResponse.getData());
  }

  @Test
  void createPost_토큰_미입력() {
    // given
    List<Long> memberIds = List.of(1L, 2L);
    CreatePostDto.Request request = new CreatePostDto.Request(
        PostType.MEMBER,
        RandomValue.string(100).setNullable(false).get(),
        RandomValue.string(2000).setNullable(false).get(),
        memberIds,
        memberIds
    );

    String url = "http://localhost:" + port + "/api/v1/posts/createPost";

    AuthenticationRequiredException exception = new AuthenticationRequiredException();

    // when
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url,
        request,
        String.class
    );

    ApiResponse<CreatePostDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<CreatePostDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());
    assertNull(apiResponse.getData());
  }



  @Test
  void getPostDetails() {
    // given
    Member member = createMember();
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    boolean isFollow = RandomValue.getRandomBoolean();
    if (isFollow) {
      createFollow(member, writeMember);
    }

    String url = "http://localhost:" + port + "/api/v1/posts/getPostDetails/" + post.getId();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        String.class
    );

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postId()).isEqualTo(post.getId());
    assertThat(apiResponse.getData().writerId()).isEqualTo(writeMember.getId());
    assertThat(apiResponse.getData().title()).isEqualTo(post.getTitle());
    assertThat(apiResponse.getData().nickname()).isEqualTo(writeMember.getNickname());
    assertThat(apiResponse.getData().profileUrl()).isEqualTo(writeMember.getProfile());
    assertThat(apiResponse.getData().content()).isEqualTo(post.getContent());
    assertThat(apiResponse.getData().isFollowed()).isEqualTo(isFollow);

  }

  // 이후 읽은 게시물에 저장 여부 확인 필요
  @Test
  void getPostDetails_방송인_조회() {
    // given
    Member member = getMember();
    member.updateRole(Role.STREAMER);
    memberRepository.save(member);
    createStreamer(member);
    TokenDto tokenDto = tokenProvider.getToken(member);

    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    boolean isFollow = RandomValue.getRandomBoolean();
    if (isFollow) {
      createFollow(member, writeMember);
    }

    String url = "http://localhost:" + port + "/api/v1/posts/getPostDetails/" + post.getId();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(tokenDto.getAccessToken());
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<String> responseEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        String.class
    );

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postId()).isEqualTo(post.getId());
    assertThat(apiResponse.getData().writerId()).isEqualTo(writeMember.getId());
    assertThat(apiResponse.getData().title()).isEqualTo(post.getTitle());
    assertThat(apiResponse.getData().nickname()).isEqualTo(writeMember.getNickname());
    assertThat(apiResponse.getData().profileUrl()).isEqualTo(writeMember.getProfile());
    assertThat(apiResponse.getData().content()).isEqualTo(post.getContent());
    assertThat(apiResponse.getData().isFollowed()).isEqualTo(isFollow);

  }

  @Test
  void getPostDetails_토큰_미입력() {
    // given
    Member writeMember = createMember();
    PostType postType = RandomValue.getRandomEnum(PostType.class);
    Post post = createPost(postType, writeMember);

    String url = "http://localhost:" + port + "/api/v1/posts/getPostDetails/" + post.getId();

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
        url,
        String.class
    );

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(apiResponse.getData());
    assertThat(apiResponse.getData().postId()).isEqualTo(post.getId());
    assertThat(apiResponse.getData().writerId()).isEqualTo(writeMember.getId());
    assertThat(apiResponse.getData().title()).isEqualTo(post.getTitle());
    assertThat(apiResponse.getData().nickname()).isEqualTo(writeMember.getNickname());
    assertThat(apiResponse.getData().profileUrl()).isEqualTo(writeMember.getProfile());
    assertThat(apiResponse.getData().content()).isEqualTo(post.getContent());
    assertThat(apiResponse.getData().isFollowed()).isEqualTo(false);

  }


  @Test
  void getPostDetails_존재하지_않는_게시물() {
    // given
    Long postId = RandomValue.getRandomLong(-999, -1);

    String url = "http://localhost:" + port + "/api/v1/posts/getPostDetails/" + postId;

    NotFoundPostException exception = new NotFoundPostException();

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

    ApiResponse<GetPostDetailsDto.Response> apiResponse = gson.fromJson(
        responseEntity.getBody(),
        new TypeToken<ApiResponse<GetPostDetailsDto.Response>>() {
        }.getType()
    );

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertNull(apiResponse.getData());
    assertThat(apiResponse.getCode()).isEqualTo(exception.getCode());
    assertThat(apiResponse.getMessage()).isEqualTo(exception.getMessage());

  }

}